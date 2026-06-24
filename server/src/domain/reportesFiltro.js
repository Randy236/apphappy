/** Rango de fechas para reportes (diario / semanal / mensual). */

export function rangoFiltro(periodo, fechaRef) {
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
