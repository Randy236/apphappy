import { describe, it } from "node:test";
import assert from "node:assert/strict";
import {
  validarNombreUsuario,
  validarPin,
  validarNombreCliente,
  validarNombreCumpleanero,
  validarZona,
  validarFechaIso,
  validarMonto,
  validarEnteroNoNegativo,
  validarMotivoCancelacion,
} from "../src/domain/entradaValidacion.js";

describe("validarNombreUsuario", () => {
  it("acepta nombre con letras", () => {
    assert.equal(validarNombreUsuario("Rosisela").ok, true);
    assert.equal(validarNombreUsuario("Admin").ok, true);
  });
  it("rechaza números en el nombre", () => {
    assert.equal(validarNombreUsuario("Juan123").ok, false);
  });
  it("acepta usuarioa..usuarioe de prueba", () => {
    assert.equal(validarNombreUsuario("usuarioa").ok, true);
    assert.equal(validarNombreUsuario("usuarioe").ok, true);
  });
  it("rechaza vacío", () => {
    assert.equal(validarNombreUsuario("").ok, false);
  });
  it("rechaza nombre demasiado largo", () => {
    assert.equal(validarNombreUsuario("A".repeat(101)).ok, false);
  });
});

describe("validarPin", () => {
  it("acepta PIN de 4 a 5 dígitos", () => {
    assert.equal(validarPin("1234").ok, true);
    assert.equal(validarPin("12345").ok, true);
  });
  it("rechaza letras", () => {
    assert.equal(validarPin("12ab").ok, false);
  });
  it("rechaza menos de 4 dígitos", () => {
    assert.equal(validarPin("123").ok, false);
  });
  it("rechaza más de 5 dígitos", () => {
    assert.equal(validarPin("123456").ok, false);
  });
});

describe("validarNombreCliente", () => {
  it("acepta nombre con espacios", () => {
    assert.equal(validarNombreCliente("Juan Pérez").ok, true);
  });
  it("rechaza vacío", () => {
    assert.equal(validarNombreCliente("").ok, false);
  });
});

describe("validarFechaIso", () => {
  it("acepta fecha válida", () => {
    assert.equal(validarFechaIso("2026-06-15").ok, true);
  });
  it("rechaza formato incorrecto", () => {
    assert.equal(validarFechaIso("15/06/2026").ok, false);
  });
  it("rechaza fecha imposible", () => {
    assert.equal(validarFechaIso("2026-02-30").ok, false);
  });
});

describe("validarMonto", () => {
  it("acepta cero y positivos", () => {
    assert.equal(validarMonto(0, "monto").ok, true);
    assert.equal(validarMonto(50.5, "monto").ok, true);
  });
  it("rechaza negativo", () => {
    assert.equal(validarMonto(-1, "monto").ok, false);
  });
});

describe("validarEnteroNoNegativo", () => {
  it("acepta enteros válidos", () => {
    assert.equal(validarEnteroNoNegativo(0, "n").ok, true);
    assert.equal(validarEnteroNoNegativo(25, "n").ok, true);
  });
  it("rechaza decimal", () => {
    assert.equal(validarEnteroNoNegativo(1.5, "n").ok, false);
  });
  it("rechaza valor mayor al máximo", () => {
    assert.equal(validarEnteroNoNegativo(10001, "n").ok, false);
  });
});

describe("validarNombreUsuario caracteres especiales", () => {
  it("rechaza símbolos no permitidos", () => {
    assert.equal(validarNombreUsuario("Juan@").ok, false);
  });
});

describe("validarNombreCliente largo", () => {
  it("rechaza nombre demasiado largo", () => {
    assert.equal(validarNombreCliente("A".repeat(201)).ok, false);
  });
  it("rechaza caracteres no permitidos", () => {
    assert.equal(validarNombreCliente("Juan@").ok, false);
  });
});

describe("validarNombreCumpleanero", () => {
  it("rechaza vacío", () => {
    assert.equal(validarNombreCumpleanero("").ok, false);
    assert.equal(validarNombreCumpleanero(null).ok, false);
  });
  it("acepta nombre válido", () => {
    assert.equal(validarNombreCumpleanero("María").ok, true);
  });
});

describe("validarZona", () => {
  it("acepta zona válida", () => {
    assert.equal(validarZona("Salón A").ok, true);
  });
  it("rechaza vacío", () => {
    assert.equal(validarZona("").ok, false);
  });
  it("rechaza zona demasiado larga", () => {
    assert.equal(validarZona("A".repeat(121)).ok, false);
  });
});

describe("validarMonto máximo", () => {
  it("rechaza monto excesivo", () => {
    assert.equal(validarMonto(1_000_000, "monto").ok, false);
  });
});

describe("validarMotivoCancelacion", () => {
  it("acepta motivo válido", () => {
    assert.equal(validarMotivoCancelacion("Cliente canceló").ok, true);
  });
  it("rechaza motivo muy corto", () => {
    assert.equal(validarMotivoCancelacion("a").ok, false);
  });
  it("rechaza motivo demasiado largo", () => {
    assert.equal(validarMotivoCancelacion("A".repeat(501)).ok, false);
  });
});
