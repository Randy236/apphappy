import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { pool } from "./db.js";

dotenv.config();

const PORT = Number(process.env.PORT || 3000);
const JWT_SECRET = process.env.JWT_SECRET || "dev_secret_cambiar";

const SALONES_VALIDOS = [
  "Ex Salón de Pinturas",
  "Salón Principal",
  "Salón de Eventos Grande",
  "Salón Laser",
];

const app = express();
app.use(cors());
app.use(express.json());

/** Prueba rápida desde el navegador del celular: http://IP_DE_TU_PC:3000/health */
app.get("/health", (req, res) => {
  res.json({ ok: true, service: "happy-jump-api" });
});

function calcularEstadoCancha(adelanto, montoTotal) {
  const a = Number(adelanto);
  const m = Number(montoTotal);
  if (a > 0 && a < m) return "con_adelanto";
  return "ocupado";
}

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
      "SELECT active_token FROM usuarios WHERE id = ? LIMIT 1",
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

/** Rango de fechas según filtro (fecha base = hoy o la referida) */
function rangoFiltro(periodo, fechaRef) {
  const base = fechaRef ? new Date(fechaRef + "T12:00:00") : new Date();
  const pad = (n) => String(n).padStart(2, "0");
  const iso = (dt) =>
    `${dt.getFullYear()}-${pad(dt.getMonth() + 1)}-${pad(dt.getDate())}`;

  if (periodo === "diario") {
    return { inicio: iso(base), fin: iso(base) };
  }
  if (periodo === "semanal") {
    const d = new Date(base);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    const monday = new Date(d);
    monday.setDate(diff);
    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);
    return { inicio: iso(monday), fin: iso(sunday) };
  }
  const first = new Date(base.getFullYear(), base.getMonth(), 1);
  const last = new Date(base.getFullYear(), base.getMonth() + 1, 0);
  return { inicio: iso(first), fin: iso(last) };
}

function hoyIsoLocal() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

function ahoraMinLocal() {
  const d = new Date();
  return d.getHours() * 60 + d.getMinutes();
}

function timeToMin(t) {
  const [h, m] = String(t).slice(0, 8).split(":");
  return Number(h) * 60 + Number(m);
}

// --- Auth ---
app.post("/auth/login", async (req, res) => {
  const { nombre, pin } = req.body || {};
  if (!nombre || pin == null) {
    return res.status(400).json({ error: "Nombre y PIN obligatorios" });
  }
  const [rows] = await pool.query(
    "SELECT id, nombre, pin, rol, active_token FROM usuarios WHERE nombre = ? LIMIT 1",
    [String(nombre).trim()]
  );
  if (!rows.length) {
    return res.status(401).json({ error: "Credenciales incorrectas" });
  }
  const u = rows[0];
  const ok = await bcrypt.compare(String(pin), u.pin);
  if (!ok) {
    return res.status(401).json({ error: "Credenciales incorrectas" });
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
  if (pinNuevo == null || String(pinNuevo).length < 4) {
    return res.status(400).json({ error: "PIN nuevo inválido (mín. 4 dígitos)" });
  }
  const [rows] = await pool.query("SELECT pin FROM usuarios WHERE id = ?", [id]);
  if (!rows.length) return res.status(404).json({ error: "Usuario no encontrado" });
  if (req.user.rol !== "administrador") {
    const ok = await bcrypt.compare(String(pinActual || ""), rows[0].pin);
    if (!ok) return res.status(400).json({ error: "PIN actual incorrecto" });
  }
  const hash = await bcrypt.hash(String(pinNuevo), 10);
  await pool.query("UPDATE usuarios SET pin = ? WHERE id = ?", [hash, id]);
  res.json({ ok: true });
});

// --- Cancha ---
app.get("/reservas-cancha", authMiddleware, async (req, res) => {
  const { desde, hasta } = req.query;
  let sql = `SELECT id, nombreCliente, deporte, fecha, hora, montoTotal, adelanto, estado FROM reservas_cancha WHERE 1=1`;
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
  const nombreCliente = String(b.nombreCliente || "").trim();
  const deporte = b.deporte;
  const fecha = b.fecha;
  const hora = b.hora;
  const montoTotal = Number(b.montoTotal);
  const adelanto = Number(b.adelanto ?? 0);

  if (!nombreCliente || !fecha || !hora) {
    return res.status(400).json({ error: "Campos obligatorios: nombreCliente, fecha, hora" });
  }
  if (!["Futbol", "Voley"].includes(deporte)) {
    return res.status(400).json({ error: "Deporte debe ser Futbol o Voley" });
  }
  if (Number.isNaN(montoTotal) || montoTotal < 0) {
    return res.status(400).json({ error: "montoTotal inválido" });
  }
  if (Number.isNaN(adelanto) || adelanto < 0) {
    return res.status(400).json({ error: "adelanto inválido" });
  }
  const hoy = hoyIsoLocal();
  if (fecha < hoy) {
    return res.status(400).json({ error: "No se puede reservar cancha para fechas pasadas" });
  }
  if (fecha === hoy && timeToMin(hora) <= ahoraMinLocal()) {
    return res.status(400).json({ error: "No se puede reservar cancha en horas pasadas" });
  }

  const estado = calcularEstadoCancha(adelanto, montoTotal);

  try {
    const [r] = await pool.query(
      `INSERT INTO reservas_cancha (nombreCliente, deporte, fecha, hora, montoTotal, adelanto, estado)
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [nombreCliente, deporte, fecha, hora, montoTotal, adelanto, estado]
    );
    const [inserted] = await pool.query(
      "SELECT * FROM reservas_cancha WHERE id = ?",
      [r.insertId]
    );
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
    "SELECT id, montoTotal, adelanto FROM reservas_cancha WHERE id = ?",
    [id]
  );
  if (!rows.length) {
    return res.status(404).json({ error: "Reserva no encontrada" });
  }
  const mt = Number(rows[0].montoTotal);
  const estado = calcularEstadoCancha(mt, mt);
  await pool.query(
    "UPDATE reservas_cancha SET adelanto = ?, estado = ? WHERE id = ?",
    [mt, estado, id]
  );
  const [updated] = await pool.query("SELECT * FROM reservas_cancha WHERE id = ?", [
    id,
  ]);
  res.json(updated[0]);
});

// --- Salones ---
app.get("/reservas-salones", authMiddleware, async (req, res) => {
  const { salon, desde, hasta } = req.query;
  let sql = `SELECT * FROM reservas_salones WHERE 1=1`;
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

function horasSeTraslapen(inicioA, finA, inicioB, finB) {
  const toMin = (t) => {
    const [h, m] = String(t).slice(0, 8).split(":");
    return Number(h) * 60 + Number(m);
  };
  const a1 = toMin(inicioA);
  const a2 = toMin(finA);
  const b1 = toMin(inicioB);
  const b2 = toMin(finB);
  return a1 < b2 && b1 < a2;
}

app.post("/reservas-salones", authMiddleware, trabajadorOnly, async (req, res) => {
  const b = req.body || {};
  const nombreCliente = String(b.nombreCliente || "").trim();
  const tipoEvento = b.tipoEvento;
  const nombreCumpleanero = b.nombreCumpleanero
    ? String(b.nombreCumpleanero).trim()
    : null;
  const zona = String(b.zona || "").trim();
  const numeroNinos = Number(b.numeroNinos ?? 0);
  const horaInicio = b.horaInicio;
  const horaFin = b.horaFin;
  const precioTotal = Number(b.precioTotal);
  const adelanto = Number(b.adelanto ?? 0);
  const salon = b.salon;
  const fecha = b.fecha;

  if (!nombreCliente || !zona || !horaInicio || !horaFin || !fecha || !salon) {
    return res.status(400).json({ error: "Faltan campos obligatorios" });
  }
  if (!SALONES_VALIDOS.includes(salon)) {
    return res.status(400).json({ error: "Salón no válido" });
  }
  if (!["Cumpleanos", "Otro"].includes(tipoEvento)) {
    return res.status(400).json({ error: "tipoEvento: Cumpleanos u Otro" });
  }
  if (tipoEvento === "Cumpleanos" && !nombreCumpleanero) {
    return res.status(400).json({ error: "nombreCumpleanero obligatorio para cumpleaños" });
  }
  if (Number.isNaN(precioTotal) || precioTotal < 0) {
    return res.status(400).json({ error: "precioTotal inválido" });
  }
  if (timeToMin(horaFin) <= timeToMin(horaInicio)) {
    return res.status(400).json({ error: "horaFin debe ser mayor que horaInicio" });
  }
  const hoy = hoyIsoLocal();
  if (fecha < hoy) {
    return res.status(400).json({ error: "No se puede reservar salones para fechas pasadas" });
  }
  if (fecha === hoy && timeToMin(horaInicio) <= ahoraMinLocal()) {
    return res.status(400).json({ error: "No se puede reservar salones en horas pasadas" });
  }

  const [existentes] = await pool.query(
    "SELECT horaInicio, horaFin FROM reservas_salones WHERE salon = ? AND fecha = ?",
    [salon, fecha]
  );
  for (const ex of existentes) {
    if (horasSeTraslapen(horaInicio, horaFin, ex.horaInicio, ex.horaFin)) {
      return res.status(409).json({ error: "Cruce de horarios en ese salón" });
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
  const [inserted] = await pool.query("SELECT * FROM reservas_salones WHERE id = ?", [
    r.insertId,
  ]);
  res.status(201).json(inserted[0]);
});

app.put("/reservas-salones/:id/cobrar-saldo", authMiddleware, trabajadorOnly, async (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isFinite(id)) {
    return res.status(400).json({ error: "id inválido" });
  }
  const [rows] = await pool.query(
    "SELECT id, precioTotal, adelanto FROM reservas_salones WHERE id = ?",
    [id]
  );
  if (!rows.length) {
    return res.status(404).json({ error: "Reserva no encontrada" });
  }
  const pt = Number(rows[0].precioTotal);
  await pool.query("UPDATE reservas_salones SET adelanto = ? WHERE id = ?", [pt, id]);
  const [updated] = await pool.query("SELECT * FROM reservas_salones WHERE id = ?", [
    id,
  ]);
  res.json(updated[0]);
});

// --- Reportes (admin) ---
app.get("/reportes", authMiddleware, adminOnly, async (req, res) => {
  const periodo = req.query.periodo || "semanal";
  const fechaRef = req.query.fecha;
  if (!["diario", "semanal", "mensual"].includes(periodo)) {
    return res.status(400).json({ error: "periodo: diario | semanal | mensual" });
  }
  const { inicio, fin } = rangoFiltro(periodo, fechaRef);

  const [ingCancha] = await pool.query(
    `SELECT COALESCE(SUM(montoTotal),0) AS total FROM reservas_cancha WHERE fecha BETWEEN ? AND ?`,
    [inicio, fin]
  );
  const [countCancha] = await pool.query(
    `SELECT COUNT(*) AS n FROM reservas_cancha WHERE fecha BETWEEN ? AND ?`,
    [inicio, fin]
  );

  const [countSalones] = await pool.query(
    `SELECT COUNT(*) AS n FROM reservas_salones WHERE fecha BETWEEN ? AND ?`,
    [inicio, fin]
  );

  const [adelCancha] = await pool.query(
    `SELECT COALESCE(SUM(adelanto),0) AS t FROM reservas_cancha WHERE fecha BETWEEN ? AND ?`,
    [inicio, fin]
  );
  const [adelSalones] = await pool.query(
    `SELECT COALESCE(SUM(adelanto),0) AS t FROM reservas_salones WHERE fecha BETWEEN ? AND ?`,
    [inicio, fin]
  );

  const wk = rangoFiltro("semanal", fechaRef);
  const [ingCanchaSemana] = await pool.query(
    `SELECT COALESCE(SUM(montoTotal),0) AS total FROM reservas_cancha WHERE fecha BETWEEN ? AND ?`,
    [wk.inicio, wk.fin]
  );
  const [ingCanchaMes] = await pool.query(
    `SELECT COALESCE(SUM(montoTotal),0) AS total FROM reservas_cancha 
     WHERE fecha >= DATE_FORMAT(CURDATE(), '%Y-%m-01') AND fecha <= LAST_DAY(CURDATE())`
  );

  const [horasOcupadas] = await pool.query(
    `SELECT TIME_FORMAT(hora, '%H:%i') AS hora, COUNT(*) AS reservas 
     FROM reservas_cancha WHERE fecha BETWEEN ? AND ?
     GROUP BY hora ORDER BY reservas DESC, hora LIMIT 8`,
    [inicio, fin]
  );

  const [ingPorSalon] = await pool.query(
    `SELECT salon, COALESCE(SUM(precioTotal),0) AS ingresos, COUNT(*) AS numReservas
     FROM reservas_salones WHERE fecha BETWEEN ? AND ?
     GROUP BY salon`,
    [inicio, fin]
  );

  const [salonMasAlquilado] = await pool.query(
    `SELECT salon, COUNT(*) AS n FROM reservas_salones WHERE fecha BETWEEN ? AND ?
     GROUP BY salon ORDER BY n DESC LIMIT 1`,
    [inicio, fin]
  );

  const [salonMasRentable] = await pool.query(
    `SELECT salon, COALESCE(SUM(precioTotal),0) AS total FROM reservas_salones WHERE fecha BETWEEN ? AND ?
     GROUP BY salon ORDER BY total DESC LIMIT 1`,
    [inicio, fin]
  );

  const [ingSalonesTotal] = await pool.query(
    `SELECT COALESCE(SUM(precioTotal),0) AS total FROM reservas_salones WHERE fecha BETWEEN ? AND ?`,
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

// --- Seed inicial ---
async function seed() {
  const [c] = await pool.query("SELECT COUNT(*) AS n FROM usuarios");
  if (c[0].n > 0) return;
  const hashAdmin = await bcrypt.hash("1234", 10);
  const hashWorker = await bcrypt.hash("1234", 10);
  await pool.query(
    "INSERT INTO usuarios (nombre, pin, rol) VALUES (?, ?, ?), (?, ?, ?)",
    ["Admin", hashAdmin, "administrador", "Juan Perez", hashWorker, "trabajador"]
  );
  console.log("Usuarios seed: Admin / Juan Perez — PIN 1234");
}

app.use((err, req, res, next) => {
  console.error(err);
  res.status(500).json({ error: "Error interno" });
});

seed()
  .then(() => {
    app.listen(PORT, "0.0.0.0", () => {
      console.log(`Happy Jump API http://0.0.0.0:${PORT}`);
    });
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
