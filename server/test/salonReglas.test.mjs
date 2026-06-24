import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { horasSeTraslapen, salonValido } from "../src/domain/salonReglas.js";

const SALONES = ["Salón Principal", "Salón Laser"];

describe("horasSeTraslapen", () => {
  it("solape parcial", () => {
    assert.equal(horasSeTraslapen("10:00", "12:00", "11:00", "13:00"), true);
  });
  it("sin solape", () => {
    assert.equal(horasSeTraslapen("10:00", "11:00", "11:00", "12:00"), false);
  });
  it("contenido dentro", () => {
    assert.equal(horasSeTraslapen("09:00", "18:00", "10:00", "11:00"), true);
  });
});

describe("salonValido", () => {
  it("acepta salón de la lista", () => {
    assert.equal(salonValido("Salón Principal", SALONES), true);
  });
  it("rechaza desconocido o vacío", () => {
    assert.equal(salonValido("Otro", SALONES), false);
    assert.equal(salonValido("  ", SALONES), false);
  });
});
