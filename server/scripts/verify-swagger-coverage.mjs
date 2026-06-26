/**
 * Verifica que cada ruta Express en index.js esté documentada en openapi.json
 * Uso: npm run swagger:verify
 */
import { readFileSync } from "fs";
import { dirname, join } from "path";
import { fileURLToPath } from "url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const indexSrc = readFileSync(join(root, "src", "index.js"), "utf8");
const spec = JSON.parse(readFileSync(join(root, "openapi.json"), "utf8"));

const routeRe = /app\.(get|post|put|delete|patch)\(\s*["']([^"']+)["']/g;
const routes = [];
let m;
while ((m = routeRe.exec(indexSrc)) !== null) {
  routes.push({ method: m[1].toUpperCase(), path: m[2] });
}

/** Express: /usuarios/:id/pin → OpenAPI: /usuarios/{id}/pin */
function toOpenApiPath(expressPath) {
  return expressPath.replace(/:([A-Za-z_][A-Za-z0-9_]*)/g, "{$1}");
}

function pathInSpec(method, expressPath) {
  const oasPath = toOpenApiPath(expressPath);
  const item = spec.paths[oasPath];
  if (!item) return false;
  return Boolean(item[method.toLowerCase()]);
}

const missing = [];
for (const r of routes) {
  if (!pathInSpec(r.method, r.path)) missing.push(r);
}

console.log("=== Cobertura Swagger (openapi.json vs index.js) ===\n");
console.log(`Rutas en código: ${routes.length}`);
console.log(`Paths en OpenAPI: ${Object.keys(spec.paths).length}\n`);

for (const r of routes) {
  const ok = pathInSpec(r.method, r.path);
  const oas = toOpenApiPath(r.path);
  const note = oas !== r.path ? ` → ${oas}` : "";
  console.log(`${ok ? "OK" : "FALTA"}  ${r.method.padEnd(6)} ${r.path}${note}`);
}

if (missing.length === 0) {
  const pct = 100;
  console.log(`\nCobertura: ${pct}% (${routes.length}/${routes.length} operaciones)`);
  process.exit(0);
} else {
  console.log(`\nFaltan ${missing.length} operación(es).`);
  process.exit(1);
}
