/** Validación de parámetros de entrada (evita datos inválidos o abusivos). */

export const LIMITS = {
  NOMBRE_USUARIO_MAX: 100,
  NOMBRE_CLIENTE_MAX: 200,
  NOMBRE_CUMPLEANERO_MAX: 200,
  ZONA_MAX: 120,
  PIN_MIN: 4,
  PIN_MAX: 5,
  MOTIVO_MIN: 2,
  MOTIVO_MAX: 500,
  MONTO_MAX: 999_999.99,
};

const RE_NOMBRE_USUARIO = /^[\p{L}\s'.-]+$/u;
const RE_NOMBRE_PERSONA = /^[\p{L}0-9\s'.-]+$/u;

export function validarNombreUsuario(nombre) {
  const n = String(nombre ?? "").trim();
  if (!n) return { ok: false, error: "Nombre obligatorio" };
  if (n.length > LIMITS.NOMBRE_USUARIO_MAX) {
    return { ok: false, error: "Nombre demasiado largo (máx. 100 caracteres)" };
  }
  if (/\d/.test(n)) {
    return { ok: false, error: "El nombre no puede contener números" };
  }
  if (!RE_NOMBRE_USUARIO.test(n)) {
    return { ok: false, error: "El nombre contiene caracteres no permitidos" };
  }
  return { ok: true, value: n };
}

export function validarPin(pin, { min = LIMITS.PIN_MIN, max = LIMITS.PIN_MAX } = {}) {
  const p = String(pin ?? "");
  if (!/^\d+$/.test(p)) {
    return { ok: false, error: "El PIN solo puede contener números" };
  }
  if (p.length < min) {
    return { ok: false, error: `PIN inválido (mín. ${min} dígitos)` };
  }
  if (p.length > max) {
    return { ok: false, error: `PIN inválido (máx. ${max} dígitos)` };
  }
  return { ok: true, value: p };
}

export function validarNombreCliente(nombre, campo = "nombreCliente") {
  const n = String(nombre ?? "").trim();
  if (!n) return { ok: false, error: `${campo} obligatorio` };
  if (n.length > LIMITS.NOMBRE_CLIENTE_MAX) {
    return { ok: false, error: `${campo} demasiado largo (máx. 200 caracteres)` };
  }
  if (!RE_NOMBRE_PERSONA.test(n)) {
    return { ok: false, error: `${campo} contiene caracteres no permitidos` };
  }
  return { ok: true, value: n };
}

export function validarNombreCumpleanero(nombre) {
  if (nombre == null || String(nombre).trim() === "") {
    return { ok: false, error: "nombreCumpleanero obligatorio para cumpleaños" };
  }
  return validarNombreCliente(nombre, "nombreCumpleanero");
}

export function validarZona(zona) {
  const z = String(zona ?? "").trim();
  if (!z) return { ok: false, error: "zona obligatoria" };
  if (z.length > LIMITS.ZONA_MAX) {
    return { ok: false, error: "zona demasiado larga (máx. 120 caracteres)" };
  }
  if (!RE_NOMBRE_PERSONA.test(z)) {
    return { ok: false, error: "zona contiene caracteres no permitidos" };
  }
  return { ok: true, value: z };
}

export function validarFechaIso(fecha, campo = "fecha") {
  const f = String(fecha ?? "").trim();
  if (!/^\d{4}-\d{2}-\d{2}$/.test(f)) {
    return { ok: false, error: `${campo} inválida (use formato YYYY-MM-DD)` };
  }
  const [y, m, d] = f.split("-").map(Number);
  const dt = new Date(y, m - 1, d);
  if (
    dt.getFullYear() !== y ||
    dt.getMonth() !== m - 1 ||
    dt.getDate() !== d
  ) {
    return { ok: false, error: `${campo} inválida` };
  }
  return { ok: true, value: f };
}

export function validarMonto(val, campo) {
  const n = Number(val);
  if (Number.isNaN(n) || n < 0) {
    return { ok: false, error: `${campo} inválido` };
  }
  if (n > LIMITS.MONTO_MAX) {
    return { ok: false, error: `${campo} excede el máximo permitido` };
  }
  return { ok: true, value: n };
}

export function validarEnteroNoNegativo(val, campo) {
  const n = Number(val);
  if (!Number.isInteger(n) || n < 0) {
    return { ok: false, error: `${campo} inválido (entero ≥ 0)` };
  }
  if (n > 10_000) {
    return { ok: false, error: `${campo} excede el máximo permitido` };
  }
  return { ok: true, value: n };
}

export function validarMotivoCancelacion(motivo) {
  const m = String(motivo ?? "").trim();
  if (m.length < LIMITS.MOTIVO_MIN) {
    return {
      ok: false,
      error: "Escribe el motivo que dio el cliente (al menos 2 letras o números).",
    };
  }
  if (m.length > LIMITS.MOTIVO_MAX) {
    return {
      ok: false,
      error: "El motivo es demasiado largo. Acórtalo un poco (máximo 500 caracteres).",
    };
  }
  return { ok: true, value: m };
}
