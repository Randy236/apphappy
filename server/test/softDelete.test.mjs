import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { NOT_DELETED, SOFT_DELETE_TABLES } from "../src/domain/softDelete.js";

describe("softDelete", () => {
  it("NOT_DELETED filtra filas activas", () => {
    assert.equal(NOT_DELETED, "deleted_at IS NULL");
  });

  it("SOFT_DELETE_TABLES incluye entidades con deleted_at", () => {
    for (const t of ["usuarios", "reservas_cancha", "reservas_salones"]) {
      assert.ok(SOFT_DELETE_TABLES.includes(t));
    }
  });
});
