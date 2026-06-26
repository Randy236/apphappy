/**
 * Crea usuarioa..usuarioe con PIN 1234 (rol trabajador).
 * Uso: npm run usuarios:prueba
 */
import bcrypt from "bcryptjs";
import dotenv from "dotenv";
import mysql from "mysql2/promise";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const serverRoot = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(serverRoot, ".env") });

const USUARIOS = ["usuarioa", "usuariob", "usuarioc", "usuariod", "usuarioe"];
const PIN = "1234";
const ROL = "trabajador";

function sslOption() {
  if (process.env.DB_SSL === "false") return undefined;
  if (
    process.env.DB_SSL === "true" ||
    process.env.MYSQLHOST ||
    process.env.RAILWAY_ENVIRONMENT
  ) {
    return { rejectUnauthorized: false };
  }
  return undefined;
}

function poolConfig() {
  const url = process.env.MYSQL_URL || process.env.DATABASE_URL;
  if (url?.startsWith("mysql")) {
    return { uri: url, ssl: sslOption() ?? { rejectUnauthorized: false }, connectionLimit: 1 };
  }
  return {
    host: process.env.DB_HOST || process.env.MYSQLHOST || "127.0.0.1",
    port: Number(process.env.DB_PORT || process.env.MYSQLPORT || 3306),
    user: process.env.DB_USER || process.env.MYSQLUSER || "root",
    password: process.env.DB_PASSWORD || process.env.MYSQLPASSWORD || "",
    database: process.env.DB_NAME || process.env.MYSQLDATABASE || "happy_jump",
    ssl: sslOption(),
    connectionLimit: 1,
  };
}

const pool = mysql.createPool(poolConfig());

try {
  const hash = await bcrypt.hash(PIN, 10);
  for (const nombre of USUARIOS) {
    await pool.query(
      `INSERT INTO usuarios (nombre, pin, rol) VALUES (?, ?, ?)
       ON DUPLICATE KEY UPDATE pin = VALUES(pin), rol = VALUES(rol), active_token = NULL, deleted_at = NULL`,
      [nombre, hash, ROL]
    );
    console.log(`Listo: ${nombre} ù PIN ${PIN} ù rol ${ROL}`);
  }
  console.log("\nLogin en app o Postman con cualquiera de esos nombres y PIN 1234.");
} catch (e) {
  console.error("Error:", e.message);
  process.exit(1);
} finally {
  await pool.end();
}
