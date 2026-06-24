import mysql from "mysql2/promise";
import dotenv from "dotenv";

dotenv.config();

function envDb(key, fallback = "") {
  return process.env[key] || fallback;
}

/** Soporta .env local, Railway MySQL y MYSQL_URL. */
function dbConfig() {
  const url = envDb("MYSQL_URL") || envDb("DATABASE_URL");
  if (url && url.startsWith("mysql")) {
    return { uri: url, ssl: sslOption() };
  }
  return {
    host:
      envDb("DB_HOST") ||
      envDb("MYSQLHOST") ||
      envDb("MYSQL_HOST") ||
      "127.0.0.1",
    port: Number(
      envDb("DB_PORT") || envDb("MYSQLPORT") || envDb("MYSQL_PORT") || 3306,
    ),
    user:
      envDb("DB_USER") ||
      envDb("MYSQLUSER") ||
      envDb("MYSQL_USER") ||
      "root",
    password:
      envDb("DB_PASSWORD") ||
      envDb("MYSQLPASSWORD") ||
      envDb("MYSQL_PASSWORD") ||
      "",
    database:
      envDb("DB_NAME") ||
      envDb("MYSQLDATABASE") ||
      envDb("MYSQL_DATABASE") ||
      "happy_jump",
    ssl: sslOption(),
  };
}

function sslOption() {
  if (envDb("DB_SSL") === "false") return undefined;
  if (
    envDb("DB_SSL") === "true" ||
    envDb("MYSQLHOST") ||
    envDb("RAILWAY_ENVIRONMENT")
  ) {
    return { rejectUnauthorized: false };
  }
  return undefined;
}

const cfg = dbConfig();

export const pool = cfg.uri
  ? mysql.createPool({ uri: cfg.uri, ssl: cfg.ssl, waitForConnections: true, connectionLimit: 10 })
  : mysql.createPool({
      host: cfg.host,
      port: cfg.port,
      user: cfg.user,
      password: cfg.password,
      database: cfg.database,
      ssl: cfg.ssl,
      waitForConnections: true,
      connectionLimit: 10,
    });
