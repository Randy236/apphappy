# Swagger UI — Happy Jump API

Documentación interactiva de la API (similar al Swagger del profesor, pero para **Happy Jump**).

## Cómo verlo

1. MySQL encendido y base `happy_jump` creada.
2. Arrancar la API:

```powershell
cd D:\apphappy-full\server
npm install
npm start
```

3. Abrir en el navegador:

**http://localhost:3000/swagger-ui/**

(o **http://localhost:3000/swagger-ui/index.html**)

## Probar endpoints protegidos

1. Expandir **POST /auth/login**
2. **Try it out** → body:

```json
{
  "nombre": "Admin",
  "pin": "1234"
}
```

3. Copiar el `token` de la respuesta.
4. Clic en **Authorize** (candado arriba) → pegar: `Bearer TU_TOKEN` o solo el token (según versión).
5. Probar **GET /reservas-cancha**, **GET /reportes**, etc.

## Archivos

| Archivo | Rol |
|---------|-----|
| `server/openapi.json` | Especificación OpenAPI 3 |
| `server/src/swagger.js` | Monta Swagger UI en Express |

## Puerto 8080 (opcional)

Si quieres la misma URL que el ejemplo Java del profesor:

```powershell
$env:PORT = "8080"
npm start
```

Swagger: **http://localhost:8080/swagger-ui/**

## Diferencia con el ejemplo del profesor

| Profesor (Spring) | Happy Jump (Node) |
|-------------------|-------------------|
| `/v1/api/empresa` | `/reservas-cancha`, `/reservas-salones` |
| Puerto 8080 | Puerto 3000 (o 8080 con `$env:PORT`) |
| SpringDoc automático | `openapi.json` + swagger-ui-express |
