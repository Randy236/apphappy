import { describe, it } from "node:test";
import assert from "node:assert/strict";
import {
  duracionCanchaMinutos,
  duracionCanchaEfectiva,
  canchaIntervaloValido,
  intervalosCanchaSeTraslapan,
} from "../src/domain/canchaReglas.js";

describe("duracionCanchaMinutos", () => {
  it(":30 → 30 min", () => {
    assert.equal(duracionCanchaMinutos("10:30:00"), 30);
  });
  it(":00 → 60 min", () => {
    assert.equal(duracionCanchaMinutos("10:00"), 60);
  });
});

describe("duracionCanchaEfectiva", () => {
  it("respeta duracion explícita 30 o 60", () => {
    assert.equal(duracionCanchaEfectiva("10:00", 30), 30);
    assert.equal(duracionCanchaEfectiva("10:00", 60), 60);
  });
  it("ignora duración inválida y usa regla de hora", () => {
    assert.equal(duracionCanchaEfectiva("10:30", 99), 30);
    assert.equal(duracionCanchaEfectiva("10:00", ""), 60);
  });
});

describe("canchaIntervaloValido", () => {
  it("8:00–22:00 con bloque estándar", () => {
    assert.equal(canchaIntervaloValido("2026-05-28", "08:00", null), true);
    assert.equal(canchaIntervaloValido("2026-05-28", "21:00", null), true);
  });
  it("rechaza antes de 8:00 o fin de juego después de 22:00", () => {
    assert.equal(canchaIntervaloValido("2026-05-28", "07:00", null), false);
    assert.equal(canchaIntervaloValido("2026-05-28", "22:00", null), false);
  });
  it(":30 solo permite 30 min", () => {
    assert.equal(canchaIntervaloValido("2026-05-28", "10:30", null), true);
    assert.equal(canchaIntervaloValido("2026-05-28", "10:30", 60), false);
  });
  it("hora en punto permite 30 o 60", () => {
    assert.equal(canchaIntervaloValido("2026-05-28", "13:00", 30), true);
    assert.equal(canchaIntervaloValido("2026-05-28", "13:00", null), true);
  });
});

describe("intervalosCanchaSeTraslapan", () => {
  it("detecta solape", () => {
    assert.equal(intervalosCanchaSeTraslapan(480, 540, 510, 570), true);
  });
  it("adyacentes no se traslapan", () => {
    assert.equal(intervalosCanchaSeTraslapan(480, 540, 540, 600), false);
  });
});
