# Auditoría — tabla de cambios en base de datos

## Qué pide el profesor

Una **tabla en MySQL** donde quede registrado **cualquier cambio**: crear, editar, eliminar, login, etc., con **quién**, **cuándo** y **qué** se modificó.

---

## Qué implementamos

Tabla **`auditoria`** con columnas:

| Columna | Significado |
|---------|-------------|
| `fecha_hora` | Cuándo ocurrió |
| `usuario_id` / `usuario_nombre` | Quién lo hizo |
| `accion` | CREAR, ELIMINAR, CANCELAR, COBRAR_SALDO, CAMBIAR_PIN, LOGIN, LOGOUT |
| `tabla` | Tabla afectada (`usuarios`, `reservas_cancha`, `reservas_salones`) |
| `registro_id` | ID del registro |
| `detalle` | JSON con datos del cambio (sin PINs ni tokens) |
| `endpoint` | Ruta HTTP usada |
| `ip_origen` | IP del cliente |

Cada operación importante en la API llama a `registrarAuditoria()` automáticamente.

---

## PASO 1 — Migración (una vez)

```powershell
cd D:\apphappy-full\server
npm run migrate:010
```

---

## PASO 2 — Verificar

```powershell
npm run auditoria:verify
```

---

## PASO 3 — Demostrar al profesor

### A) Hacer un cambio (Swagger)

1. Laragon + `npm start`
2. `npm run k6:prepare` (liberar sesiones)
3. Swagger → **POST /auth/login** (Admin / 1234) → **Authorize**
4. **DELETE /reservas-cancha/{id}** o **POST /reservas-cancha** (cualquier cambio)

### B) Ver la auditoría

**Opción 1 — Swagger:**  
**GET /auditoria** → Execute → lista JSON con todos los movimientos.

**Opción 2 — phpMyAdmin:**  
Laragon → phpMyAdmin → `happy_jump` → tabla **`auditoria`** → Browse.

**Capturas recomendadas:**

1. Estructura de la tabla `auditoria` en phpMyAdmin  
2. Filas después de un DELETE / CREAR / LOGIN  
3. **GET /auditoria** en Swagger con respuesta 200  

---

## Texto para Word (copiar)

> Se implementó una tabla **`auditoria`** en MySQL que registra automáticamente las operaciones de la API: creación y eliminación lógica de reservas, cancelaciones, cobro de saldo, cambio de PIN, login y logout. Cada fila guarda fecha, usuario, acción, tabla afectada, ID del registro y detalle en JSON. El administrador consulta el historial con **GET /auditoria** o directamente en la base de datos.

---

## Archivos del repo

| Archivo | Rol |
|---------|-----|
| `server/migrations/010_auditoria.sql` | Crea la tabla |
| `server/src/domain/auditoria.js` | Función `registrarAuditoria` |
| `server/src/index.js` | Registra en cada cambio |
| `server/scripts/verify-auditoria.mjs` | Verificación |

---

## Checklist

```
[ ] npm run migrate:010
[ ] npm run auditoria:verify
[ ] Cambio en Swagger (crear o eliminar)
[ ] Captura tabla auditoria en phpMyAdmin
[ ] Captura GET /auditoria en Swagger
```
