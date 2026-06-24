import { timeToMin } from "./timeUtil.js";

export function duracionCanchaMinutos(horaStr) {
  return timeToMin(horaStr) % 60 === 30 ? 30 : 60;
}

export function duracionCanchaEfectiva(horaStr, duracionMinutos) {
  if (duracionMinutos != null && duracionMinutos !== "") {
    const d = Number(duracionMinutos);
    if (d === 30 || d === 60) return d;
  }
  return duracionCanchaMinutos(horaStr);
}

export function canchaIntervaloValido(fecha, hora, duracionMinutos) {
  void fecha;
  const min = timeToMin(hora);
  const dur = duracionCanchaEfectiva(hora, duracionMinutos);
  const fin = min + dur;
  if (min < 8 * 60 || fin > 22 * 60) return false;
  if (min > 21 * 60 + 30) return false;
  if (min % 60 === 30) return dur === 30;
  if (dur === 30) return true;
  return dur === 60;
}

export function intervalosCanchaSeTraslapan(aStart, aEnd, bStart, bEnd) {
  return aStart < bEnd && bStart < aEnd;
}
