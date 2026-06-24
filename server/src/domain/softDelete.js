/**
 * Eliminado lógico: filas con deleted_at IS NULL están activas.
 */

export const NOT_DELETED = "deleted_at IS NULL";

/** Tablas con soft delete */
export const SOFT_DELETE_TABLES = [
  "usuarios",
  "reservas_cancha",
  "reservas_salones",
];
