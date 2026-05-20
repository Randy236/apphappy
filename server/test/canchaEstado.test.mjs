import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { calcularEstadoCancha } from "../src/domain/canchaEstado.js";

describe("calcularEstadoCancha", () => {
  it("con adelanto parcial → con_adelanto", () => {
    assert.equal(calcularEstadoCancha(100, 500), "con_adelanto");
  });

  it("sin adelanto → ocupado", () => {
    assert.equal(calcularEstadoCancha(0, 500), "ocupado");
  });

  it("adelanto igual al total → ocupado", () => {
    assert.equal(calcularEstadoCancha(500, 500), "ocupado");
  });

  it("acepta strings numéricos", () => {
    assert.equal(calcularEstadoCancha("50", "200"), "con_adelanto");
  });
});
