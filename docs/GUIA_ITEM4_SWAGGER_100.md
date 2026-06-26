# Ítem 4 — Documentación Swagger (cobertura 100 %)

## Qué pide el profesor

Que la **API REST** esté documentada en **Swagger / OpenAPI** y que **todos** los endpoints (controllers/rutas) aparezcan en Swagger UI.

En Happy Jump la API está en `server/` y la especificación en `server/openapi.json`.

---

## Buena noticia

El proyecto **ya tiene el 100 %** de las rutas de `index.js` en `openapi.json` (**16 operaciones** HTTP).

Tu trabajo en el ítem 4 es **demostrarlo** con capturas y el comando de verificación.

---

## PASO 1 — Levantar la API (2 min)

```powershell
cd D:\apphappy-full\server
npm install
npm start
```

Debe quedar escuchando en el puerto **3000**.

---

## PASO 2 — Abrir Swagger UI (1 min)

Navegador:

**http://localhost:3000/swagger-ui/**

También:

- JSON crudo: http://localhost:3000/openapi.json

---

## PASO 3 — Verificar cobertura 100 % (1 min)

Otra PowerShell (con la API corriendo o solo el archivo):

```powershell
cd D:\apphappy-full\server
npm run swagger:verify
```

Debe salir:

```
Cobertura: 100% (16/16 operaciones)
```

y todas las líneas con **OK**.

---

## PASO 4 — Probar con Authorize (5 min)

1. En Swagger UI → **POST /auth/login** → **Try it out**  
2. Body:

```json
{
  "nombre": "Admin",
  "pin": "1234"
}
```

3. **Execute** → copia el `token` de la respuesta.  
4. Clic en **Authorize** (candado arriba) → pega: `Bearer TU_TOKEN` (o solo el token si Swagger lo acepta).  
5. Prueba **GET /reservas-cancha** → Execute → debe responder 200.

---

## PASO 5 — Lista para el informe (tabla)

| # | Método | Ruta | Tag Swagger |
|---|--------|------|-------------|
| 1 | GET | /health | Sistema |
| 2 | GET | /hello | Pruebas k6 |
| 3 | GET | /sumar | Pruebas k6 |
| 4 | POST | /auth/login | Autenticación |
| 5 | POST | /auth/logout | Autenticación |
| 6 | PUT | /usuarios/{id}/pin | Autenticación |
| 7 | GET | /reservas-cancha | Reservas Cancha |
| 8 | POST | /reservas-cancha | Reservas Cancha |
| 9 | PUT | /reservas-cancha/{id}/cobrar-saldo | Reservas Cancha |
| 10 | PUT | /reservas-cancha/{id}/cancelar | Reservas Cancha |
| 11 | GET | /reservas-salones | Reservas Salones |
| 12 | POST | /reservas-salones | Reservas Salones |
| 13 | PUT | /reservas-salones/{id}/cobrar-saldo | Reservas Salones |
| 14 | PUT | /reservas-salones/{id}/cancelar | Reservas Salones |
| 15 | GET | /reportes | Reportes |
| 16 | GET | /reportes/cancelaciones | Reportes |

**Total: 16 operaciones HTTP documentadas (100 %).**

---

## PASO 6 — Capturas para el profesor (10 min)

Carpeta: `D:\apphappy-full\docs\evidencias-swagger\`

| # | Captura |
|---|---------|
| 1 | Swagger UI mostrando **todos** los tags desplegados |
| 2 | Salida de `npm run swagger:verify` con **100%** |
| 3 | **POST /auth/login** ejecutado con respuesta 200 |
| 4 | Un endpoint protegido (ej. GET /reservas-cancha) con 200 |
| 5 | Archivo `openapi.json` en el repo (GitHub) |

---

## PASO 7 — Texto para el informe (copiar)

> La API Happy Jump se documentó con **OpenAPI 3.0** (`server/openapi.json`) y **Swagger UI** en `/swagger-ui/`, siguiendo el estilo de documentación tipo Springdoc. La cobertura de documentación es del **100 %** de las rutas implementadas en `server/src/index.js` (16 operaciones), verificada con el script `npm run swagger:verify`. Los endpoints protegidos usan esquema **Bearer JWT** obtenido en `POST /auth/login`.

---

## Archivos del repo

| Archivo | Rol |
|---------|-----|
| `server/openapi.json` | Especificación OpenAPI |
| `server/src/swagger.js` | Monta UI y `/openapi.json` |
| `server/scripts/verify-swagger-coverage.mjs` | Verificación automática |
| `docs/SWAGGER_HAPPY_JUMP.md` | Guía rápida |

---

## Checklist ítem 4

```
[ ] API corriendo (npm start)
[ ] Swagger UI abre en /swagger-ui/
[ ] npm run swagger:verify → 100%
[ ] Login + Authorize probado
[ ] 5 capturas en evidencias-swagger/
[ ] (Opcional) PDF o sección en informe de avance
```

---

## Si agregas una ruta nueva

1. Implementa en `server/src/index.js`  
2. Añade la misma ruta en `server/openapi.json`  
3. Ejecuta `npm run swagger:verify` hasta ver 100 %

---

Cuando tengas las capturas, di **"listo el 4"** → ítem 5 (cobertura tests 100 %).
