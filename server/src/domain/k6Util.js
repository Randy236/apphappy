/** Validación para endpoints ligeros de k6 (/sumar). */

export function sumarQuery(a, b) {
  const na = Number(a);
  const nb = Number(b);
  if (Number.isNaN(na) || Number.isNaN(nb)) {
    return { ok: false };
  }
  return { ok: true, result: String(na + nb) };
}
