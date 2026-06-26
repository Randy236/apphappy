/**
 * Verifica que k6/api-all-endpoints.js referencia todas las rutas de index.js
 * Uso: npm run k6:verify  (desde server/) o node server/scripts/verify-k6-coverage.mjs
 */
import { readFileSync } from "fs";
import { dirname, join } from "path";
import { fileURLToPath } from "url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const repo = join(root, "..");
const indexSrc = readFileSync(join(root, "src", "index.js"), "utf8");
const k6Src = readFileSync(join(repo, "k6", "api-all-endpoints.js"), "utf8");

const routeRe = /app\.(get|post|put|delete|patch)\(\s*["']([^"']+)["']/g;
const routes = [];
let m;
while ((m = routeRe.exec(indexSrc)) !== null) {
  routes.push({ method: m[1].toUpperCase(), path: m[2] });
}

/** Fragmentos que el script k6 debe mencionar por ruta */
function markersForRoute(path) {
  const base = path.split("/:")[0];
  if (path.includes(":id/cancelar")) return [base, "cancelar"];
  if (path.includes(":id/cobrar-saldo")) return [base, "cobrar-saldo"];
  if (path.includes(":id/pin")) return ["usuarios", "pin"];
  return [path];
}

const missing = [];
console.log("=== Cobertura k6 (api-all-endpoints.js vs index.js) ===\n");
for (const r of routes) {
  const markers = markersForRoute(r.path);
  const ok = markers.every((mk) => k6Src.includes(mk));
  console.log(`${ok ? "OK" : "FALTA"}  ${r.method.padEnd(6)} ${r.path}`);
  if (!ok) missing.push(r);
}

if (missing.length === 0) {
  console.log(`\nCobertura: 100% (${routes.length}/${routes.length} rutas en script k6)`);
  console.log("Ejecutar prueba: k6 run k6/api-all-endpoints.js (API + MySQL activos)");
  process.exit(0);
}
console.log(`\nFaltan ${missing.length} ruta(s) en el script k6.`);
process.exit(1);
