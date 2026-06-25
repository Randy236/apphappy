/**
 * Verifica que todos los módulos en src/domain/ tengan archivo de test
 * y que npm test pase. Para cobertura de líneas usar: npm run test:coverage
 */
import { readdirSync, existsSync } from "node:fs";
import { join, dirname } from "node:path";
import { fileURLToPath } from "node:url";
import { spawnSync } from "node:child_process";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const domainDir = join(root, "src", "domain");
const testDir = join(root, "test");

const domainFiles = readdirSync(domainDir).filter((f) => f.endsWith(".js"));
const testFiles = readdirSync(testDir).filter((f) => f.endsWith(".test.mjs"));

const baseName = (f) => f.replace(/\.js$/, "").replace(/\.test\.mjs$/, "");
const tested = new Set(testFiles.map(baseName));

const missing = domainFiles
  .map((f) => baseName(f))
  .filter((name) => !tested.has(name));

if (missing.length) {
  console.error("Faltan tests para domain:", missing.join(", "));
  process.exit(1);
}

const run = spawnSync("npm", ["test"], { cwd: root, shell: true, stdio: "inherit" });
if (run.status !== 0) process.exit(run.status ?? 1);

console.log(`\n✓ Domain: ${domainFiles.length} módulos, ${testFiles.length} archivos de test, npm test OK`);
console.log("  Cobertura de líneas: ejecuta npm run test:coverage y revisa el resumen Node.");
