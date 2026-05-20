/**
 * Regla de negocio: estado de cancha según adelanto y monto total.
 * Extraído para pruebas unitarias (Node test) y reutilización en la API.
 */
export function calcularEstadoCancha(adelanto, montoTotal) {
  const a = Number(adelanto);
  const m = Number(montoTotal);
  if (a > 0 && a < m) return "con_adelanto";
  return "ocupado";
}
