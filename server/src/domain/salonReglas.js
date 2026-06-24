import { timeToMin } from "./timeUtil.js";

export function horasSeTraslapen(inicioA, finA, inicioB, finB) {
  const a1 = timeToMin(inicioA);
  const a2 = timeToMin(finA);
  const b1 = timeToMin(inicioB);
  const b2 = timeToMin(finB);
  return a1 < b2 && b1 < a2;
}

export function salonValido(nombre, salonesValidos) {
  return salonesValidos.includes(String(nombre || "").trim());
}
