import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { sanitizarDetalle, ACCIONES_AUDITORIA } from "../src/domain/auditoria.js";

describe("auditoria", () => {
  it("sanitizarDetalle oculta pin y token", () => {
    const out = sanitizarDetalle({ pin: "1234", token: "abc", nombre: "Juan" });
    assert.equal(out.pin, "[oculto]");
    assert.equal(out.token, "[oculto]");
    assert.equal(out.nombre, "Juan");
  });

  it("ACCIONES_AUDITORIA incluye operaciones clave", () => {
    for (const a of ["CREAR", "ELIMINAR", "LOGIN", "CANCELAR"]) {
      assert.ok(ACCIONES_AUDITORIA.includes(a));
    }
  });
});
