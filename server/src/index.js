import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { spawn } from "node:child_process";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { pool } from "./db.js";
import { setupSwagger } from "./swagger.js";
import { calcularEstadoCancha } from "./domain/canchaEstado.js";
import {
  duracionCanchaEfectiva,
  canchaIntervaloValido,
  intervalosCanchaSeTraslapan,
} from "./domain/canchaReglas.js";
import { rangoFiltro } from "./domain/reportesFiltro.js";
import { horasSeTraslapen, salonValido } from "./domain/salonReglas.js";
import { hoyIsoLocal, ahoraMinLocal, timeToMin } from "./domain/timeUtil.js";
import { sumarQuery } from "./domain/k6Util.js";
import { NOT_DELETED } from "./domain/softDelete.js";
import { registrarAuditoria } from "./domain/auditoria.js";
import {
  validarNombreUsuario,
  validarPin,
  validarNombreCliente,
  validarNombreCumpleanero,
  validarZona,
  validarFechaIso,
  validarMonto,
  validarEnteroNoNegativo,
  validarMotivoCancelacion,
} from "./domain/entradaValidacion.js";

dotenv.config();

const PORT = Number(process.env.PORT || 3000);
const isProd = process.env.NODE_ENV === "production";
const JWT_SECRET = process.env.JWT_SECRET || (isProd ? null : "dev_secret_cambiar");
if (!JWT_SECRET) {
  console.error("Falta JWT_SECRET en entorno (obligatorio en producción).");
  process.exit(1);
}

const SALONES_VALIDOS = [
  "Ex Salón de Pinturas",
  "Salón Principal",
  "Salón de Eventos Grande",
  "Salón Laser",
];

const app = express();
app.use(cors());
app.use(express.json());
setupSwagger(app, PORT);

/** Comprobación de disponibilidad: GET /health */
app.get("/health", (req, res) => {
  res.json({ ok: true, service: "happy-jump-api" });
});

/** CD: GitHub Actions → POST /internal/deploy con Bearer HAPPYJUMP_DEPLOY_TOKEN */
app.post("/internal/deploy", (req, res) => {
  const secret = process.env.HAPPYJUMP_DEPLOY_TOKEN;
  if (!secret) {
    return res.status(503).json({ error: "Deploy webhook no configurado" });
  }
  const auth = req.headers.authorization || "";
  if (auth !== `Bearer ${secret}`) {
    return res.status(401).json({ error: "No autorizado" });
  }
  const script = join(
    dirname(fileURLToPath(import.meta.url)),
    "..",
    "scripts",
    "prod-deploy.sh",
  );
  const child = spawn("sh", [script], {
    detached: true,
    stdio: "ignore",
    env: process.env,
  });
  child.unref();
  res.status(202).json({ ok: true, message: "Deploy iniciado" });
});

/** Endpoints ligeros para k6 (sin auth, sin DB) — ver docs/TUTORIAL_K6_HAPPY_JUMP.md */
app.get("/hello", (req, res) => {
  res.type("text").send("Good Morning");
});

app.get("/sumar", (req, res) => {
  const out = sumarQuery(req.query.a, req.query.b);
  if (!out.ok) return res.status(400).type("text").send("error");
  res.type("text").send(out.result);
});

function authMiddleware(req, res, next) {
  const h = req.headers.authorization;
  if (!h || !h.startsWith("Bearer ")) {
    return res.status(401).json({ error: "No autorizado" });
  }
  const token = h.slice(7);
  let payload;
  try {
    payload = jwt.verify(token, JWT_SECRET);
  } catch {
    return res.status(401).json({ error: "Token inválido" });
  }
  pool
    .query(
      `SELECT active_token FROM usuarios WHERE id = ? AND ${NOT_DELETED} LIMIT 1`,
      [payload.id]
    )
    .then(([rows]) => {
      if (!rows.length) {
        return res.status(401).json({ error: "No autorizado" });
      }
      const stored = rows[0].active_token;
      if (!stored || stored !== token) {
        return res.status(401).json({
          error: "Sesión no válida o iniciada en otro dispositivo",
        });
      }
      req.user = payload;
      next();
    })
    .catch((err) => {
      console.error(err);
      res.status(500).json({ error: "Error interno" });
    });
}

function adminOnly(req, res, next) {
  if (req.user?.rol !== "administrador") {
    return res.status(403).json({ error: "Solo administrador" });
  }
  next();
}

function trabajadorOnly(req, res, next) {
  if (req.user?.rol !== "trabajador") {
    return res.status(403).json({
      error: "Solo los trabajadores pueden registrar reservas en salones",
    });
  }
  next();
}

/** Eliminado lógico: UPDATE deleted_at (nunca DELETE FROM). */
async function marcarEliminado(tabla, id, req, res) {
  const permitidas = ["usuarios", "reservas_cancha", "reservas_salones"];
  if (!permitidas.includes(tabla)) {
    return res.status(500).json({ error: "Tabla no válida" });
  }
  const [r] = await pool.query(
    `UPDATE ${tabla} SET deleted_at = NOW(3) WHERE id = ? AND deleted_at IS NULL`,
    [id]
  );
  if (!r.affectedRows) {
    return res.status(404).json({ error: "Registro no encontrado o ya eliminado" });
  }
  await registrarAuditoria(pool, {
    usuarioId: req.user?.id,
    usuarioNombre: req.user?.nombre,
    accion: "ELIMINAR",
    tabla,
    registroId: id,
    detalle: { tipo: "eliminado_logico" },
    req,
  });
  res.json({ ok: true, id, eliminadoLogico: true });
}

// --- Auth ---
app.post("/auth/login", async (req, res) => {
  const { nombre, pin } = req.body || {};
  const vNombre = validarNombreUsuario(nombre);
  if (!vNombre.ok) {
    return res.status(400).json({ error: vNombre.error });
  }
  if (pin == null || pin === "") {
    return res.status(400).json({ error: "PIN obligatorio" });
  }
  const vPin = validarPin(pin);
  if (!vPin.ok) {
    return res.status(400).json({ error: vPin.error });
  }
  const [rows] = await pool.query(
    `SELECT id, nombre, pin, rol, active_token FROM usuarios WHERE nombre = ? AND ${NOT_DELETED} LIMIT 1`,
    [vNombre.value]
  );
  if (!rows.length) {
    return res.status(401).json({
      error: "El nombre o el PIN no coinciden. Revísalos e inténtalo otra vez.",
    });
  }
  const u = rows[0];
  const ok = await bcrypt.compare(vPin.value, u.pin);
  if (!ok) {
    return res.status(401).json({
      error: "El nombre o el PIN no coinciden. Revísalos e inténtalo otra vez.",
    });
  }
  if (u.active_token != null && String(u.active_token).length > 0) {
    return res.status(409).json({
      error: "Este usuario ya está activo en otro dispositivo",
    });
  }
  const token = jwt.sign(
    { id: u.id, nombre: u.nombre, rol: u.rol },
    JWT_SECRET,
    { expiresIn: "7d" }
  );
  await pool.query("UPDATE usuarios SET active_token = ? WHERE id = ?", [
    token,
    u.id,
  ]);
  await registrarAuditoria(pool, {
    usuarioId: u.id,
    usuarioNombre: u.nombre,
    accion: "LOGIN",
    tabla: "usuarios",
    registroId: u.id,
    detalle: { rol: u.rol },
    req,
  });
  res.json({
    token,
    usuario: { id: u.id, nombre: u.nombre, rol: u.rol },
  });
});

app.post("/auth/logout", authMiddleware, async (req, res) => {
  try {
    await pool.query("UPDATE usuarios SET active_token = NULL WHERE id = ?", [
      req.user.id,
    ]);
    await registrarAuditoria(pool, {
      usuarioId: req.user.id,
      usuarioNombre: req.user.nombre,
      accion: "LOGOUT",
      tabla: "usuarios",
      registroId: req.user.id,
      req,
    });
    res.json({ ok: true });
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: "Error interno" });
  }
});

app.put("/usuarios/:id/pin", authMiddleware, async (req, res) => {
  const id = Number(req.params.id);
  const { pinActual, pinNuevo } = req.body || {};
  if (req.user.id !== id && req.user.rol !== "administrador") {
    return res.status(403).json({ error: "No permitido" });
  }
  if (pinNuevo == null || pinNuevo === "") {
    return res.status(400).json({ error: "PIN nuevo obligatorio" });
  }
  const vPinNuevo = validarPin(pinNuevo);
  if (!vPinNuevo.ok) {
    return res.status(400).json({ error: vPinNuevo.error });
  }
  let pinActualValido = null;
  if (req.user.rol !== "administrador") {
    const vPinActual = validarPin(pinActual ?? "");
    if (!vPinActual.ok) {
      return res.status(400).json({ error: vPinActual.error });
    }
    pinActualValido = vPinActual.value;
  }
  const [rows] = await pool.query(
    `SELECT pin FROM usuarios WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  if (!rows.length) return res.status(404).json({ error: "Usuario no encontrado" });
  if (req.user.rol !== "administrador") {
    const ok = await bcrypt.compare(pinActualValido, rows[0].pin);
    if (!ok) return res.status(400).json({ error: "PIN actual incorrecto" });
  }
  const hash = await bcrypt.hash(vPinNuevo.value, 10);
  await pool.query("UPDATE usuarios SET pin = ? WHERE id = ?", [hash, id]);
  await registrarAuditoria(pool, {
    usuarioId: req.user.id,
    usuarioNombre: req.user.nombre,
    accion: "CAMBIAR_PIN",
    tabla: "usuarios",
    registroId: id,
    detalle: { usuarioAfectado: id },
    req,
  });
  res.json({ ok: true });
});

// --- Cancha ---
app.get("/reservas-cancha", authMiddleware, async (req, res) => {
  const { desde, hasta } = req.query;
  let sql = `SELECT id, nombreCliente, deporte, fecha, hora, montoTotal, adelanto, estado, motivo_cancelacion, duracion_minutos FROM reservas_cancha WHERE ${NOT_DELETED}`;
  const params = [];
  if (desde) {
    sql += " AND fecha >= ?";
    params.push(desde);
  }
  if (hasta) {
    sql += " AND fecha <= ?";
    params.push(hasta);
  }
  sql += " ORDER BY fecha, hora";
  const [rows] = await pool.query(sql, params);
  res.json(rows);
});

app.post("/reservas-cancha", authMiddleware, async (req, res) => {
  const b = req.body || {};
  const vNombre = validarNombreCliente(b.nombreCliente);
  if (!vNombre.ok) {
    return res.status(400).json({ error: vNombre.error });
  }
  const nombreCliente = vNombre.value;
  const deporte = b.deporte;
  const vFecha = validarFechaIso(b.fecha);
  if (!vFecha.ok) {
    return res.status(400).json({ error: vFecha.error });
  }
  const fecha = vFecha.value;
  const hora = b.hora;
  const vMonto = validarMonto(b.montoTotal, "montoTotal");
  if (!vMonto.ok) {
    return res.status(400).json({ error: vMonto.error });
  }
  const montoTotal = vMonto.value;
  const vAdelanto = validarMonto(b.adelanto ?? 0, "adelanto");
  if (!vAdelanto.ok) {
    return res.status(400).json({ error: vAdelanto.error });
  }
  const adelanto = vAdelanto.value;
  if (adelanto > montoTotal) {
    return res.status(400).json({ error: "El adelanto no puede ser mayor que el monto total" });
  }
  let duracionMinutos = b.duracionMinutos;
  if (duracionMinutos != null && duracionMinutos !== "") {
    duracionMinutos = Number(duracionMinutos);
    if (![30, 60].includes(duracionMinutos)) {
      return res.status(400).json({ error: "duracionMinutos debe ser 30 o 60" });
    }
  } else {
    duracionMinutos = null;
  }

  if (!hora) {
    return res.status(400).json({ error: "hora obligatoria" });
  }
  if (!["Futbol", "Voley"].includes(deporte)) {
    return res.status(400).json({ error: "Deporte debe ser Futbol o Voley" });
  }
  const hoy = hoyIsoLocal();
  if (fecha < hoy) {
    return res.status(400).json({ error: "No se puede reservar cancha para fechas pasadas" });
  }
  if (fecha === hoy && timeToMin(hora) < ahoraMinLocal()) {
    return res.status(400).json({
      error: "Esa hora de inicio ya pasó. Elige un horario a partir de ahora.",
    });
  }

  if (!canchaIntervaloValido(fecha, hora, duracionMinutos)) {
    return res.status(400).json({
      error:
        "Horario no válido. La cancha es de 8:00 a 22:00. Tramos de 30 min (inicio :30 o cierre en :30) o 1 h; ejemplo 11:00–13:30 = dos horas + media.",
    });
  }

  const [existentesCancha] = await pool.query(
    `SELECT hora, duracion_minutos FROM reservas_cancha WHERE fecha = ? AND estado <> 'cancelado' AND ${NOT_DELETED}`,
    [fecha]
  );
  const nStart = timeToMin(hora);
  const nEnd = nStart + duracionCanchaEfectiva(hora, duracionMinutos);
  for (const ex of existentesCancha) {
    const eStart = timeToMin(ex.hora);
    const eEnd = eStart + duracionCanchaEfectiva(ex.hora, ex.duracion_minutos);
    if (intervalosCanchaSeTraslapan(nStart, nEnd, eStart, eEnd)) {
      return res.status(409).json({
        error: "Ese horario ya está ocupado: se cruza con otra reserva de cancha.",
      });
    }
  }

  const estado = calcularEstadoCancha(adelanto, montoTotal);

  try {
    const [r] = await pool.query(
      `INSERT INTO reservas_cancha (nombreCliente, deporte, fecha, hora, montoTotal, adelanto, estado, duracion_minutos)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      [nombreCliente, deporte, fecha, hora, montoTotal, adelanto, estado, duracionMinutos]
    );
    const [inserted] = await pool.query(
      `SELECT * FROM reservas_cancha WHERE id = ? AND ${NOT_DELETED}`,
      [r.insertId]
    );
    await registrarAuditoria(pool, {
      usuarioId: req.user.id,
      usuarioNombre: req.user.nombre,
      accion: "CREAR",
      tabla: "reservas_cancha",
      registroId: r.insertId,
      detalle: {
        nombreCliente,
        deporte,
        fecha,
        hora,
        montoTotal,
        adelanto,
      },
      req,
    });
    res.status(201).json(inserted[0]);
  } catch (e) {
    if (e.code === "ER_DUP_ENTRY") {
      return res.status(409).json({ error: "Ese horario ya está reservado" });
    }
    throw e;
  }
});

app.put("/reservas-cancha/:id/cobrar-saldo", authMiddleware, async (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  const [rows] = await pool.query(
    `SELECT id, montoTotal, adelanto, estado FROM reservas_cancha WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  if (!rows.length) {
    return res.status(404).json({ error: "Reserva no encontrada" });
  }
  if (rows[0].estado === "cancelado") {
    return res.status(400).json({
      error: "Esta reserva ya fue cancelada; no se puede registrar un cobro sobre ella.",
    });
  }
  const mt = Number(rows[0].montoTotal);
  const estado = calcularEstadoCancha(mt, mt);
  await pool.query(
    "UPDATE reservas_cancha SET adelanto = ?, estado = ? WHERE id = ?",
    [mt, estado, id]
  );
  await registrarAuditoria(pool, {
    usuarioId: req.user.id,
    usuarioNombre: req.user.nombre,
    accion: "COBRAR_SALDO",
    tabla: "reservas_cancha",
    registroId: id,
    detalle: { montoTotal: mt, adelantoAnterior: Number(rows[0].adelanto) },
    req,
  });
  const [updated] = await pool.query(
    `SELECT * FROM reservas_cancha WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  res.json(updated[0]);
});

app.put("/reservas-cancha/:id/cancelar", authMiddleware, async (req, res) => {
  const id = Number(req.params.id);
  const vMotivo = validarMotivoCancelacion((req.body || {}).motivo);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  if (!vMotivo.ok) {
    return res.status(400).json({ error: vMotivo.error });
  }
  const motivo = vMotivo.value;
  const [rows] = await pool.query(
    `SELECT id, estado FROM reservas_cancha WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  if (!rows.length) {
    return res.status(404).json({ error: "Reserva no encontrada" });
  }
  if (rows[0].estado === "cancelado") {
    return res.status(400).json({ error: "Esta reserva ya figura como cancelada." });
  }
  await pool.query(
    "UPDATE reservas_cancha SET estado = 'cancelado', motivo_cancelacion = ? WHERE id = ?",
    [motivo, id]
  );
  await registrarAuditoria(pool, {
    usuarioId: req.user.id,
    usuarioNombre: req.user.nombre,
    accion: "CANCELAR",
    tabla: "reservas_cancha",
    registroId: id,
    detalle: { motivo },
    req,
  });
  const [updated] = await pool.query(
    `SELECT * FROM reservas_cancha WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  res.json(updated[0]);
});

app.delete("/reservas-cancha/:id", authMiddleware, adminOnly, async (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  return marcarEliminado("reservas_cancha", id, req, res);
});

// --- Salones ---
app.get("/reservas-salones", authMiddleware, async (req, res) => {
  const { salon, desde, hasta } = req.query;
  let sql = `SELECT * FROM reservas_salones WHERE ${NOT_DELETED}`;
  const params = [];
  if (salon) {
    sql += " AND salon = ?";
    params.push(salon);
  }
  if (desde) {
    sql += " AND fecha >= ?";
    params.push(desde);
  }
  if (hasta) {
    sql += " AND fecha <= ?";
    params.push(hasta);
  }
  sql += " ORDER BY fecha, horaInicio";
  const [rows] = await pool.query(sql, params);
  res.json(rows);
});

app.post("/reservas-salones", authMiddleware, trabajadorOnly, async (req, res) => {
  const b = req.body || {};
  const vNombre = validarNombreCliente(b.nombreCliente);
  if (!vNombre.ok) {
    return res.status(400).json({ error: vNombre.error });
  }
  const nombreCliente = vNombre.value;
  const tipoEvento = b.tipoEvento;
  const vZona = validarZona(b.zona);
  if (!vZona.ok) {
    return res.status(400).json({ error: vZona.error });
  }
  const zona = vZona.value;
  const vNinos = validarEnteroNoNegativo(b.numeroNinos ?? 0, "numeroNinos");
  if (!vNinos.ok) {
    return res.status(400).json({ error: vNinos.error });
  }
  const numeroNinos = vNinos.value;
  const horaInicio = b.horaInicio;
  const horaFin = b.horaFin;
  const vPrecio = validarMonto(b.precioTotal, "precioTotal");
  if (!vPrecio.ok) {
    return res.status(400).json({ error: vPrecio.error });
  }
  const precioTotal = vPrecio.value;
  const vAdelanto = validarMonto(b.adelanto ?? 0, "adelanto");
  if (!vAdelanto.ok) {
    return res.status(400).json({ error: vAdelanto.error });
  }
  const adelanto = vAdelanto.value;
  if (adelanto > precioTotal) {
    return res.status(400).json({ error: "El adelanto no puede ser mayor que el precio total" });
  }
  const salon = b.salon;
  const vFecha = validarFechaIso(b.fecha);
  if (!vFecha.ok) {
    return res.status(400).json({ error: vFecha.error });
  }
  const fecha = vFecha.value;
  let nombreCumpleanero = null;

  if (!horaInicio || !horaFin || !salon) {
    return res.status(400).json({ error: "Faltan campos obligatorios" });
  }
  if (!salonValido(salon, SALONES_VALIDOS)) {
    return res.status(400).json({ error: "Salón no válido" });
  }
  if (!["Cumpleanos", "Otro"].includes(tipoEvento)) {
    return res.status(400).json({ error: "tipoEvento: Cumpleanos u Otro" });
  }
  if (tipoEvento === "Cumpleanos") {
    const vCumple = validarNombreCumpleanero(b.nombreCumpleanero);
    if (!vCumple.ok) {
      return res.status(400).json({ error: vCumple.error });
    }
    nombreCumpleanero = vCumple.value;
  }
  if (timeToMin(horaFin) <= timeToMin(horaInicio)) {
    return res.status(400).json({ error: "horaFin debe ser mayor que horaInicio" });
  }
  const hoy = hoyIsoLocal();
  if (fecha < hoy) {
    return res.status(400).json({ error: "No se puede reservar salones para fechas pasadas" });
  }
  if (fecha === hoy && timeToMin(horaInicio) < ahoraMinLocal()) {
    return res.status(400).json({
      error: "Esa hora de inicio ya pasó. Elige un horario a partir de ahora.",
    });
  }

  const [existentes] = await pool.query(
    `SELECT horaInicio, horaFin FROM reservas_salones WHERE salon = ? AND fecha = ? AND cancelada = 0 AND ${NOT_DELETED}`,
    [salon, fecha]
  );
  for (const ex of existentes) {
    if (horasSeTraslapen(horaInicio, horaFin, ex.horaInicio, ex.horaFin)) {
      return res.status(409).json({
        error: "Ese tramo de hora ya está reservado en este salón. Elige otro horario.",
      });
    }
  }

  const [r] = await pool.query(
    `INSERT INTO reservas_salones (nombreCliente, tipoEvento, nombreCumpleanero, zona, numeroNinos, horaInicio, horaFin, precioTotal, adelanto, salon, fecha)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [
      nombreCliente,
      tipoEvento,
      tipoEvento === "Cumpleanos" ? nombreCumpleanero : null,
      zona,
      numeroNinos,
      horaInicio,
      horaFin,
      precioTotal,
      adelanto,
      salon,
      fecha,
    ]
  );
  const [inserted] = await pool.query(
    `SELECT * FROM reservas_salones WHERE id = ? AND ${NOT_DELETED}`,
    [r.insertId]
  );
  await registrarAuditoria(pool, {
    usuarioId: req.user.id,
    usuarioNombre: req.user.nombre,
    accion: "CREAR",
    tabla: "reservas_salones",
    registroId: r.insertId,
    detalle: { nombreCliente, salon, fecha, precioTotal, adelanto },
    req,
  });
  res.status(201).json(inserted[0]);
});

app.put("/reservas-salones/:id/cobrar-saldo", authMiddleware, trabajadorOnly, async (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  const [rows] = await pool.query(
    `SELECT id, precioTotal, adelanto, cancelada FROM reservas_salones WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  if (!rows.length) {
    return res.status(404).json({ error: "Reserva no encontrada" });
  }
  if (Number(rows[0].cancelada)) {
    return res.status(400).json({
      error: "Esta reserva ya fue cancelada; no se puede registrar un cobro sobre ella.",
    });
  }
  const pt = Number(rows[0].precioTotal);
  await pool.query("UPDATE reservas_salones SET adelanto = ? WHERE id = ?", [pt, id]);
  await registrarAuditoria(pool, {
    usuarioId: req.user.id,
    usuarioNombre: req.user.nombre,
    accion: "COBRAR_SALDO",
    tabla: "reservas_salones",
    registroId: id,
    detalle: { precioTotal: pt, adelantoAnterior: Number(rows[0].adelanto) },
    req,
  });
  const [updated] = await pool.query(
    `SELECT * FROM reservas_salones WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  res.json(updated[0]);
});

app.put("/reservas-salones/:id/cancelar", authMiddleware, async (req, res) => {
  const id = Number(req.params.id);
  const vMotivo = validarMotivoCancelacion((req.body || {}).motivo);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  if (!vMotivo.ok) {
    return res.status(400).json({ error: vMotivo.error });
  }
  const motivo = vMotivo.value;
  const [rows] = await pool.query(
    `SELECT id, cancelada FROM reservas_salones WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  if (!rows.length) {
    return res.status(404).json({ error: "Reserva no encontrada" });
  }
  if (Number(rows[0].cancelada)) {
    return res.status(400).json({ error: "Esta reserva ya figura como cancelada." });
  }
  await pool.query(
    "UPDATE reservas_salones SET cancelada = 1, motivo_cancelacion = ? WHERE id = ?",
    [motivo, id]
  );
  await registrarAuditoria(pool, {
    usuarioId: req.user.id,
    usuarioNombre: req.user.nombre,
    accion: "CANCELAR",
    tabla: "reservas_salones",
    registroId: id,
    detalle: { motivo },
    req,
  });
  const [updated] = await pool.query(
    `SELECT * FROM reservas_salones WHERE id = ? AND ${NOT_DELETED}`,
    [id]
  );
  res.json(updated[0]);
});

app.delete("/reservas-salones/:id", authMiddleware, adminOnly, async (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  return marcarEliminado("reservas_salones", id, req, res);
});

// --- Reportes (admin) ---
app.get("/reportes", authMiddleware, adminOnly, async (req, res) => {
  const periodo = req.query.periodo || "semanal";
  const fechaRef = req.query.fecha;
  if (!["diario", "semanal", "mensual"].includes(periodo)) {
    return res.status(400).json({ error: "periodo: diario | semanal | mensual" });
  }
  const { inicio, fin } = rangoFiltro(periodo, fechaRef);

  const filtroCanchaActiva = `estado <> 'cancelado' AND ${NOT_DELETED}`;
  const filtroSalonActivo = `cancelada = 0 AND ${NOT_DELETED}`;

  const [ingCancha] = await pool.query(
    `SELECT COALESCE(SUM(montoTotal),0) AS total FROM reservas_cancha WHERE fecha BETWEEN ? AND ? AND ${filtroCanchaActiva}`,
    [inicio, fin]
  );
  const [countCancha] = await pool.query(
    `SELECT COUNT(*) AS n FROM reservas_cancha WHERE fecha BETWEEN ? AND ? AND ${filtroCanchaActiva}`,
    [inicio, fin]
  );

  const [countSalones] = await pool.query(
    `SELECT COUNT(*) AS n FROM reservas_salones WHERE fecha BETWEEN ? AND ? AND ${filtroSalonActivo}`,
    [inicio, fin]
  );

  const [adelCancha] = await pool.query(
    `SELECT COALESCE(SUM(adelanto),0) AS t FROM reservas_cancha WHERE fecha BETWEEN ? AND ? AND ${filtroCanchaActiva}`,
    [inicio, fin]
  );
  const [adelSalones] = await pool.query(
    `SELECT COALESCE(SUM(adelanto),0) AS t FROM reservas_salones WHERE fecha BETWEEN ? AND ? AND ${filtroSalonActivo}`,
    [inicio, fin]
  );

  const wk = rangoFiltro("semanal", fechaRef);
  const [ingCanchaSemana] = await pool.query(
    `SELECT COALESCE(SUM(montoTotal),0) AS total FROM reservas_cancha WHERE fecha BETWEEN ? AND ? AND ${filtroCanchaActiva}`,
    [wk.inicio, wk.fin]
  );
  const [ingCanchaMes] = await pool.query(
    `SELECT COALESCE(SUM(montoTotal),0) AS total FROM reservas_cancha 
     WHERE fecha >= DATE_FORMAT(CURDATE(), '%Y-%m-01') AND fecha <= LAST_DAY(CURDATE()) AND ${filtroCanchaActiva}`
  );

  const [horasOcupadas] = await pool.query(
    `SELECT TIME_FORMAT(hora, '%H:%i') AS hora, COUNT(*) AS reservas 
     FROM reservas_cancha WHERE fecha BETWEEN ? AND ? AND ${filtroCanchaActiva}
     GROUP BY hora ORDER BY reservas DESC, hora LIMIT 8`,
    [inicio, fin]
  );

  const [ingPorSalon] = await pool.query(
    `SELECT salon, COALESCE(SUM(precioTotal),0) AS ingresos, COUNT(*) AS numReservas
     FROM reservas_salones WHERE fecha BETWEEN ? AND ? AND ${filtroSalonActivo}
     GROUP BY salon`,
    [inicio, fin]
  );

  const [salonMasAlquilado] = await pool.query(
    `SELECT salon, COUNT(*) AS n FROM reservas_salones WHERE fecha BETWEEN ? AND ? AND ${filtroSalonActivo}
     GROUP BY salon ORDER BY n DESC LIMIT 1`,
    [inicio, fin]
  );

  const [salonMasRentable] = await pool.query(
    `SELECT salon, COALESCE(SUM(precioTotal),0) AS total FROM reservas_salones WHERE fecha BETWEEN ? AND ? AND ${filtroSalonActivo}
     GROUP BY salon ORDER BY total DESC LIMIT 1`,
    [inicio, fin]
  );

  const [ingSalonesTotal] = await pool.query(
    `SELECT COALESCE(SUM(precioTotal),0) AS total FROM reservas_salones WHERE fecha BETWEEN ? AND ? AND ${filtroSalonActivo}`,
    [inicio, fin]
  );

  const ingC = Number(ingCancha[0].total);
  const ingS = Number(ingSalonesTotal[0].total);
  const adelTotal =
    Number(adelCancha[0].t) + Number(adelSalones[0].t);

  res.json({
    periodo,
    rango: { inicio, fin },
    resumen: {
      ingresosTotales: ingC + ingS,
      totalReservasCancha: Number(countCancha[0].n),
      totalReservasSalones: Number(countSalones[0].n),
      totalAdelantos: adelTotal,
    },
    comparacionIngresos: {
      cancha: ingC,
      salones: ingS,
    },
    cancha: {
      ingresosPeriodo: ingC,
      ingresosSemanales: Number(ingCanchaSemana[0].total),
      ingresosMensuales: Number(ingCanchaMes[0].total),
      totalReservas: Number(countCancha[0].n),
      horariosMasOcupados: horasOcupadas,
    },
    salones: {
      ingresosTotalPeriodo: ingS,
      porSalon: ingPorSalon.map((row) => ({
        salon: row.salon,
        ingresos: Number(row.ingresos),
        reservas: Number(row.numReservas),
      })),
      salonMasAlquilado: salonMasAlquilado[0] || null,
      salonMasRentable: salonMasRentable[0] || null,
    },
  });
});

/** Cancelaciones (cancha y salones) en el mismo rango que los reportes — solo administrador. */
app.get("/reportes/cancelaciones", authMiddleware, adminOnly, async (req, res) => {
  const periodo = req.query.periodo || "semanal";
  const fechaRef = req.query.fecha;
  if (!["diario", "semanal", "mensual"].includes(periodo)) {
    return res.status(400).json({ error: "periodo: diario | semanal | mensual" });
  }
  const { inicio, fin } = rangoFiltro(periodo, fechaRef);

  const [canchaRows] = await pool.query(
    `SELECT id, nombreCliente, deporte, fecha, 
            TIME_FORMAT(hora, '%H:%i') AS hora,
            motivo_cancelacion
     FROM reservas_cancha
     WHERE estado = 'cancelado' AND ${NOT_DELETED} AND fecha BETWEEN ? AND ?
     ORDER BY fecha DESC, hora DESC`,
    [inicio, fin]
  );

  const [salonRows] = await pool.query(
    `SELECT id, nombreCliente, salon, fecha,
            TIME_FORMAT(horaInicio, '%H:%i') AS horaInicio,
            TIME_FORMAT(horaFin, '%H:%i') AS horaFin,
            tipoEvento, motivo_cancelacion
     FROM reservas_salones
     WHERE cancelada = 1 AND ${NOT_DELETED} AND fecha BETWEEN ? AND ?
     ORDER BY fecha DESC, horaInicio DESC`,
    [inicio, fin]
  );

  res.json({
    rango: { inicio, fin },
    cancha: canchaRows,
    salones: salonRows,
  });
});

app.delete("/usuarios/:id", authMiddleware, adminOnly, async (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  if (req.user.id === id) {
    return res.status(400).json({ error: "No puedes eliminarte a ti mismo" });
  }
  return marcarEliminado("usuarios", id, req, res);
});

// --- Auditoría (solo admin) ---
app.get("/auditoria", authMiddleware, adminOnly, async (req, res) => {
  const { tabla, accion, desde, hasta, limite } = req.query;
  let sql = `SELECT id, fecha_hora, usuario_id, usuario_nombre, accion, tabla, registro_id, detalle, endpoint, ip_origen
             FROM auditoria WHERE 1=1`;
  const params = [];
  if (tabla) {
    sql += " AND tabla = ?";
    params.push(String(tabla));
  }
  if (accion) {
    sql += " AND accion = ?";
    params.push(String(accion));
  }
  if (desde) {
    sql += " AND DATE(fecha_hora) >= ?";
    params.push(desde);
  }
  if (hasta) {
    sql += " AND DATE(fecha_hora) <= ?";
    params.push(hasta);
  }
  sql += " ORDER BY fecha_hora DESC";
  const max = Math.min(Math.max(Number(limite) || 100, 1), 500);
  sql += ` LIMIT ${max}`;
  const [rows] = await pool.query(sql, params);
  res.json(rows);
});

// --- Seed inicial ---
async function seed() {
  const [c] = await pool.query(`SELECT COUNT(*) AS n FROM usuarios WHERE ${NOT_DELETED}`);
  if (c[0].n > 0) return;
  const hashAdmin = await bcrypt.hash("1234", 10);
  const hashWorker = await bcrypt.hash("1234", 10);
  await pool.query(
    "INSERT INTO usuarios (nombre, pin, rol) VALUES (?, ?, ?), (?, ?, ?)",
    ["Admin", hashAdmin, "administrador", "Rosisela", hashWorker, "trabajador"]
  );
  console.log("Usuarios seed: Admin / Rosisela — PIN 1234");
}

app.use((err, req, res, next) => {
  console.error(err);
  res.status(500).json({ error: "Error interno" });
});

seed()
  .then(() => {
    app.listen(PORT, "0.0.0.0", () => {
      console.log(`Happy Jump API http://0.0.0.0:${PORT}`);
      console.log(`Swagger UI http://localhost:${PORT}/swagger-ui/`);
    });
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
