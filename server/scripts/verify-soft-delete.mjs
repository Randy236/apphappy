/**
 * Verifica columnas deleted_at y que index.js no use DELETE FROM en tablas de negocio.
 */
import { readFileSync } from "fs";
import { dirname, join } from "path";
import { fileURLToPath } from "url";
import dotenv from "dotenv";
import mysql from "mysql2/promise";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(root, ".env") });

const indexSrc = readFileSync(join(root, "src", "index.js"), "utf8");
const tables = ["usuarios", "reservas_cancha", "reservas_salones"];

const badDelete = tables.some((t) =>
  new RegExp(`DELETE\\s+FROM\\s+${t}`, "i").test(indexSrc)
);
if (badDelete) {
  console.error("index.js contiene DELETE FROM en tablas de negocio.");
  process.exit(1);
}

if (!indexSrc.includes("NOT_DELETED") && !indexSrc.includes("deleted_at IS NULL")) {
  console.error("index.js no filtra deleted_at.");
  process.exit(1);
}

const pool = mysql.createPool({
  host: process.env.DB_HOST || "127.0.0.1",
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || "root",
  password: process.env.DB_PASSWORD || "",
  database: process.env.DB_NAME || "happy_jump",
  connectionLimit: 1,
});

try {
  const [rows] = await pool.query(
    `SELECT TABLE_NAME, COLUMN_NAME FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = ? AND COLUMN_NAME = 'deleted_at' AND TABLE_NAME IN (?)`,
    [process.env.DB_NAME || "happy_jump", tables]
  );
  const found = new Set(rows.map((r) => r.TABLE_NAME));
  const missing = tables.filter((t) => !found.has(t));
  if (missing.length) {
    console.error("Falta deleted_at en:", missing.join(", "), "— ejecuta npm run migrate:009");
    process.exit(1);
  }
  console.log("=== Eliminado lógico ===\n");
  for (const t of tables) console.log(`OK  ${t}.deleted_at`);
  console.log("\nOK  Sin DELETE FROM en index.js");
  console.log("OK  Filtros deleted_at en API");
  console.log("\nCobertura entidades: 3/3");
} catch (e) {
  console.error(e.message);
  console.error("¿MySQL encendido? npm run migrate:009");
  process.exit(1);
} finally {
  await pool.end();
}
