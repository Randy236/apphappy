/**
 * Registro de auditoría en tabla `auditoria`.
 * Acciones: CREAR, ACTUALIZAR, ELIMINAR, CANCELAR, COBRAR_SALDO, CAMBIAR_PIN, LOGIN, LOGOUT
 */

export const ACCIONES_AUDITORIA = [
  "CREAR",
  "ACTUALIZAR",
  "ELIMINAR",
  "CANCELAR",
  "COBRAR_SALDO",
  "CAMBIAR_PIN",
  "LOGIN",
  "LOGOUT",
];

/** Omite campos sensibles antes de guardar en JSON. */
export function sanitizarDetalle(detalle) {
  if (detalle == null || typeof detalle !== "object") return detalle;
  const copia = { ...detalle };
  for (const k of ["pin", "pinNuevo", "pinActual", "token", "active_token"]) {
    if (k in copia) copia[k] = "[oculto]";
  }
  return copia;
}

export async function registrarAuditoria(
  pool,
  { usuarioId = null, usuarioNombre = null, accion, tabla, registroId = null, detalle = null, req = null }
) {
  try {
    const endpoint = req ? `${req.method} ${req.originalUrl || req.path}` : null;
    const ip = req?.ip || req?.socket?.remoteAddress || null;
    const detalleJson =
      detalle != null ? JSON.stringify(sanitizarDetalle(detalle)) : null;
    await pool.query(
      `INSERT INTO auditoria (usuario_id, usuario_nombre, accion, tabla, registro_id, detalle, endpoint, ip_origen)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      [usuarioId, usuarioNombre, accion, tabla, registroId, detalleJson, endpoint, ip]
    );
  } catch (e) {
    console.error("Auditoría (no bloquea operación):", e.message);
  }
}
