/**
 * Crea un usuario en MySQL (PIN hasheado con bcrypt).
 * Uso:
 *   npm run usuario:crear -- Lizbeth
 *   npm run usuario:crear -- Lizbeth 1234 trabajador
 *   npm run usuario:crear -- Lizbeth 1234 administrador
 */
import bcrypt from "bcryptjs";
import dotenv from "dotenv";
import mysql from "mysql2/promise";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const serverRoot = join(dirname(fileURLToPath(import.meta.url)), "..");
dotenv.config({ path: join(serverRoot, ".env") });

const nombre = (process.argv[2] || "").trim();
const pin = String(process.argv[3] || "1234");
const rolArg = (process.argv[4] || "trabajador").trim().toLowerCase();

if (!nombre) {
  console.error("Uso: npm run usuario:crear -- TU_NOMBRE [PIN] [trabajador|administrador]");
  process.exit(1);
}
if (!["trabajador", "administrador"].includes(rolArg)) {
  console.error("Rol debe ser trabajador o administrador");
  process.exit(1);
}
if (pin.length < 4) {
  console.error("PIN mínimo 4 caracteres");
  process.exit(1);
}

const pool = mysql.createPool({
  host: process.env.DB_HOST || "127.0.0.1",
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || "root",
  password: process.env.DB_PASSWORD || "",
  database: process.env.DB_NAME || "happy_jump",
  connectionLimit: 1,
});

try {
  const hash = await bcrypt.hash(pin, 10);
  await pool.query(
    `INSERT INTO usuarios (nombre, pin, rol) VALUES (?, ?, ?)
     ON DUPLICATE KEY UPDATE pin = VALUES(pin), rol = VALUES(rol), active_token = NULL, deleted_at = NULL`,
    [nombre, hash, rolArg]
  );
  console.log(`Listo: usuario "${nombre}" — PIN ${pin} — rol ${rolArg}`);
  console.log("Login en app o Swagger con ese nombre y PIN.");
} catch (e) {
  console.error("Error:", e.message);
  process.exit(1);
} finally {
  await pool.end();
}
