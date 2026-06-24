import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { timeToMin, hoyIsoLocal, ahoraMinLocal } from "../src/domain/timeUtil.js";

describe("timeToMin", () => {
  it("convierte HH:MM", () => {
    assert.equal(timeToMin("10:30"), 630);
    assert.equal(timeToMin("08:00:00"), 480);
  });
});

describe("hoyIsoLocal / ahoraMinLocal", () => {
  it("formato ISO de hoy", () => {
    assert.match(hoyIsoLocal(), /^\d{4}-\d{2}-\d{2}$/);
  });
  it("minutos del día en rango válido", () => {
    const m = ahoraMinLocal();
    assert.ok(m >= 0 && m < 24 * 60);
  });
});
