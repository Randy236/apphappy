/**
 * Inserta usuarioa..usuarioe (PIN 1234).
 * Uso: npm run migrate:011
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import dotenv from "dotenv";
import mysql from "mysql2/promise";

const serverRoot = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(serverRoot, ".env") });

const sqlPath = join(serverRoot, "migrations", "011_usuarios_usuario1_a_5.sql");

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
    return { uri: url, ssl: sslOption() ?? { rejectUnauthorized: false }, multipleStatements: true, connectionLimit: 1 };
  }
  return {
    host: process.env.DB_HOST || process.env.MYSQLHOST || "127.0.0.1",
    port: Number(process.env.DB_PORT || process.env.MYSQLPORT || 3306),
    user: process.env.DB_USER || process.env.MYSQLUSER || "root",
    password: process.env.DB_PASSWORD || process.env.MYSQLPASSWORD || "",
    database: process.env.DB_NAME || process.env.MYSQLDATABASE || "happy_jump",
    ssl: sslOption(),
    multipleStatements: true,
    connectionLimit: 1,
  };
}

async function main() {
  const sql = readFileSync(sqlPath, "utf8");
  const pool = mysql.createPool(poolConfig());
  const db = process.env.DB_NAME || process.env.MYSQLDATABASE || "happy_jump";
  console.log(`Migracion 011 (usuarioa..usuarioe) en ${db}...`);
  try {
    await pool.query(sql);
    console.log("Listo: usuarioa, usuariob, usuarioc, usuariod, usuarioe - PIN 1234.");
  } catch (e) {
    console.error(e.message);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

main();
