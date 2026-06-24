/**
 * Crea tablas en MySQL (Railway u otro cloud) sin CREATE DATABASE.
 * Uso: npm run db:init
 */
import dotenv from "dotenv";
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import mysql from "mysql2/promise";

const serverRoot = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(serverRoot, ".env") });

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

function cfg() {
  const url = process.env.MYSQL_URL || process.env.DATABASE_URL;
  if (url?.startsWith("mysql")) {
    return { uri: url, ssl: sslOption() ?? { rejectUnauthorized: false } };
  }
  return {
    host: process.env.DB_HOST || process.env.MYSQLHOST || "127.0.0.1",
    port: Number(process.env.DB_PORT || process.env.MYSQLPORT || 3306),
    user: process.env.DB_USER || process.env.MYSQLUSER || "root",
    password: process.env.DB_PASSWORD || process.env.MYSQLPASSWORD || "",
    database: process.env.DB_NAME || process.env.MYSQLDATABASE || "happy_jump",
    ssl: sslOption(),
  };
}

const sqlPath = join(serverRoot, "railway-schema.sql");
const raw = readFileSync(sqlPath, "utf8");
const statements = raw
  .split(";")
  .map((s) => s.replace(/--[^\n]*/g, "").trim())
  .filter((s) => s.length > 0);

const c = cfg();
const conn = c.uri
  ? await mysql.createConnection({ uri: c.uri, ssl: c.ssl, multipleStatements: false })
  : await mysql.createConnection(c);

try {
  console.log("Inicializando esquema Happy Jump...");
  for (const stmt of statements) {
    await conn.query(stmt);
  }
  console.log("Esquema OK:", statements.length, "sentencias ejecutadas.");
} catch (e) {
  console.error("Error init DB:", e.message);
  process.exit(1);
} finally {
  await conn.end();
}
