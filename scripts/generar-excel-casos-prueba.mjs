/**
 * Genera Excel de casos de prueba Happy Jump a partir de la plantilla del profesor.
 * Uso: node scripts/generar-excel-casos-prueba.mjs
 *
 * Requiere: pip install openpyxl (una vez)
 */
import { spawnSync } from "node:child_process";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const py = join(root, "scripts", "generar-excel-casos-prueba.py");
const r = spawnSync("python", [py], { stdio: "inherit", cwd: root });
process.exit(r.status ?? 1);
