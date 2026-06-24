import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { sumarQuery } from "../src/domain/k6Util.js";

describe("sumarQuery", () => {
  it("suma números válidos", () => {
    assert.deepEqual(sumarQuery("2", "3"), { ok: true, result: "5" });
    assert.deepEqual(sumarQuery(1, 2), { ok: true, result: "3" });
  });
  it("rechaza NaN", () => {
    assert.deepEqual(sumarQuery("x", 1), { ok: false });
    assert.deepEqual(sumarQuery(undefined, 1), { ok: false });
  });
});
