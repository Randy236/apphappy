/**
 * Amplía ENUM estado en reservas_cancha para permitir 'cancelado'.
 * Uso: desde la carpeta server → npm run migrate:005
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import dotenv from "dotenv";
import mysql from "mysql2/promise";

const __dirname = dirname(fileURLToPath(import.meta.url));
const serverRoot = join(__dirname, "..");

dotenv.config({ path: join(serverRoot, ".env") });

const sqlPath = join(serverRoot, "migrations", "005_estado_cancha_enum_cancelado.sql");

async function main() {
  let sql;
  try {
    sql = readFileSync(sqlPath, "utf8");
  } catch (e) {
    console.error("No se encontró el archivo:", sqlPath);
    process.exit(1);
  }

  const pool = mysql.createPool({
    host: process.env.DB_HOST || "127.0.0.1",
    port: Number(process.env.DB_PORT || 3306),
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASSWORD || "",
    database: process.env.DB_NAME || "happy_jump",
    waitForConnections: true,
    connectionLimit: 1,
    multipleStatements: true,
  });

  console.log("Aplicando migración 005 (ENUM estado + cancelado) en", process.env.DB_NAME || "happy_jump", "…");
  try {
    await pool.query(sql);
    console.log("Listo. Vuelve a cancelar la reserva o reinicia la API: npm start");
  } catch (err) {
    console.error("Error al ejecutar SQL:", err.message);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

main();
