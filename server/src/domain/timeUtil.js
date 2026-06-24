/** Utilidades de tiempo (sin I/O) para reglas de cancha y salones. */

export function timeToMin(t) {
  const [h, m] = String(t).slice(0, 8).split(":");
  return Number(h) * 60 + Number(m);
}

export function hoyIsoLocal() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

export function ahoraMinLocal() {
  const d = new Date();
  return d.getHours() * 60 + d.getMinutes();
}
