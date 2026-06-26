/**
 * Crea tabla auditoria para trazabilidad de cambios.
 * Uso: npm run migrate:010
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import dotenv from "dotenv";
import mysql from "mysql2/promise";

const serverRoot = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(serverRoot, ".env") });

const sqlPath = join(serverRoot, "migrations", "010_auditoria.sql");

async function main() {
  const sql = readFileSync(sqlPath, "utf8");
  const pool = mysql.createPool({
    host: process.env.DB_HOST || "127.0.0.1",
    port: Number(process.env.DB_PORT || 3306),
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASSWORD || "",
    database: process.env.DB_NAME || "happy_jump",
    multipleStatements: true,
    connectionLimit: 1,
  });
  console.log("Migración 010 (auditoría) en", process.env.DB_NAME || "happy_jump", "…");
  try {
    await pool.query(sql);
    console.log("Listo: tabla auditoria creada.");
  } catch (e) {
    console.error(e.message);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

main();
