/**
 * Vacía todas las reservas (reportes en 0) y libera sesiones de usuario.
 * Uso: npm run demo:reset-ventas  (MySQL/Laragon encendido)
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import dotenv from "dotenv";
import mysql from "mysql2/promise";

const __dirname = dirname(fileURLToPath(import.meta.url));
const serverRoot = join(__dirname, "..");

dotenv.config({ path: join(serverRoot, ".env") });

const sqlPath = join(
  serverRoot,
  "migrations",
  "006_limpiar_reservas_cero_y_sesiones.sql",
);

async function main() {
  let sql;
  try {
    sql = readFileSync(sqlPath, "utf8");
  } catch (e) {
    console.error("No se encontró:", sqlPath);
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

  console.log(
    "Reset demo: vaciando reservas y liberando usuarios en",
    process.env.DB_NAME || "happy_jump",
    "…",
  );
  try {
    await pool.query(sql);
    console.log(
      "Listo: no hay reservas (cancha ni salones). Reportes quedan en 0 para la demo.",
    );
    console.log(
      "Sesiones liberadas. En la app, cierra sesión o vuelve a entrar con el PIN.",
    );
  } catch (err) {
    console.error("Error SQL:", err.message);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

main();
