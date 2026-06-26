/**
 * Añade deleted_at (eliminado lógico) en usuarios, reservas_cancha, reservas_salones.
 * Uso: npm run migrate:009
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import dotenv from "dotenv";
import mysql from "mysql2/promise";

const serverRoot = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(serverRoot, ".env") });

const sqlPath = join(serverRoot, "migrations", "009_deleted_at_soft_delete.sql");

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
  console.log("Migración 009 (deleted_at — soft delete) en", process.env.DB_NAME || "happy_jump", "…");
  try {
    await pool.query(sql);
    console.log("Listo: deleted_at en usuarios, reservas_cancha, reservas_salones.");
  } catch (e) {
    console.error(e.message);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

main();
