import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { rangoFiltro } from "../src/domain/reportesFiltro.js";

describe("rangoFiltro", () => {
  it("diario: mismo día", () => {
    const r = rangoFiltro("diario", "2026-05-15");
    assert.equal(r.inicio, "2026-05-15");
    assert.equal(r.fin, "2026-05-15");
  });

  it("semanal: lunes a domingo de la semana de la fecha", () => {
    const r = rangoFiltro("semanal", "2026-05-15");
    assert.equal(r.inicio, "2026-05-11");
    assert.equal(r.fin, "2026-05-17");
  });

  it("mensual (default): primer y último día del mes", () => {
    const r = rangoFiltro("mensual", "2026-05-15");
    assert.equal(r.inicio, "2026-05-01");
    assert.equal(r.fin, "2026-05-31");
  });

  it("sin fechaRef usa mes actual (estructura inicio/fin)", () => {
    const r = rangoFiltro("mensual");
    assert.match(r.inicio, /^\d{4}-\d{2}-\d{2}$/);
    assert.match(r.fin, /^\d{4}-\d{2}-\d{2}$/);
    assert.ok(r.inicio <= r.fin);
  });
});
