/**
 * Añade columna duracion_minutos a reservas_cancha.
 * Uso: npm run migrate:008
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import dotenv from "dotenv";
import mysql from "mysql2/promise";

const __dirname = dirname(fileURLToPath(import.meta.url));
const serverRoot = join(__dirname, "..");
dotenv.config({ path: join(serverRoot, ".env") });

const sqlPath = join(serverRoot, "migrations", "008_duracion_cancha.sql");

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
  console.log("Migración 008 (duracion_minutos cancha)…");
  try {
    await pool.query(sql);
    console.log("Listo.");
  } catch (e) {
    if (String(e.message).includes("Duplicate column")) {
      console.log("La columna ya existe; nada que hacer.");
    } else {
      console.error(e.message);
      process.exit(1);
    }
  } finally {
    await pool.end();
  }
}

main();
