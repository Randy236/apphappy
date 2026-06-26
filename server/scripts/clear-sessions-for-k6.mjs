/**
 * Libera active_token para que k6 pueda hacer login (Admin / Rosisela).
 * Uso: npm run k6:prepare
 */
import dotenv from "dotenv";
import mysql from "mysql2/promise";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const serverRoot = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(serverRoot, ".env") });

const pool = mysql.createPool({
  host: process.env.DB_HOST || "127.0.0.1",
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || "root",
  password: process.env.DB_PASSWORD || "",
  database: process.env.DB_NAME || "happy_jump",
  connectionLimit: 1,
});

try {
  const [r] = await pool.query(
    "UPDATE usuarios SET active_token = NULL WHERE active_token IS NOT NULL"
  );
  console.log(
    `Sesiones liberadas en ${process.env.DB_NAME || "happy_jump"} (filas afectadas: ${r.affectedRows}).`
  );
  console.log("Ahora ejecuta: k6 run k6/api-all-endpoints.js");
} catch (e) {
  console.error("Error:", e.message);
  console.error("¿MySQL/Laragon encendido y base happy_jump creada?");
  process.exit(1);
} finally {
  await pool.end();
}
