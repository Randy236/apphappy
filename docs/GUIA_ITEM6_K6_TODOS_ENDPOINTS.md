# Ítem 6 — k6 en todos los endpoints de la API

## Qué pide el profesor

Pruebas de **rendimiento/carga con k6** sobre **todos los controllers** (rutas) de la API REST.

---

## Archivos del proyecto

| Archivo | Uso |
|---------|-----|
| `k6/api-all-endpoints.js` | Smoke que recorre las **16 rutas** |
| `k6/smoke-test.js`, `load-test.js`, `stress-test.js` | Tutorial original (`/sumar`) |
| `server/scripts/verify-k6-coverage.mjs` | Verifica 100 % rutas en el script |
| `docs/TUTORIAL_K6_HAPPY_JUMP.md` | Instalación k6 en Windows |

---

## PASO 1 — Instalar k6

```powershell
winget install GrafanaLabs.k6 --accept-package-agreements --accept-source-agreements
k6 version
```

---

## PASO 2 — API + MySQL

```powershell
cd D:\apphappy-full\server
npm install
npm start
```

Usuarios seed: **Admin** / **Rosisela**, PIN **1234**.

> Si login falla con *“ya está activo en otro dispositivo”*: cierra sesión en la app o en phpMyAdmin:  
> `UPDATE usuarios SET active_token = NULL;`

---

## PASO 3 — Verificar script (sin correr carga)

```powershell
cd D:\apphappy-full\server
npm run k6:verify
```

Debe decir: **Cobertura: 100% (16/16 rutas)**.

---

## PASO 4 — Liberar sesiones (si falló el login)

Si k6 muestra **`POST /auth/login (Admin)` ✗** y solo 4 peticiones HTTP, casi siempre es **sesión activa** (código 409):

```powershell
cd D:\apphappy-full\server
npm run k6:prepare
```

O en phpMyAdmin: `UPDATE usuarios SET active_token = NULL;`

También puedes usar el script todo-en-uno:

```powershell
cd D:\apphappy-full
.\scripts\run-k6-all.ps1
```

(API debe estar en marcha con `npm start` en `server/`.)

---

## PASO 5 — Ejecutar smoke en todos los endpoints

```powershell
cd D:\apphappy-full
k6 run k6/api-all-endpoints.js
```

Otra PC en la red:

```powershell
k6 run -e BASE_URL=http://IP_DE_TU_PC:3000 k6/api-all-endpoints.js
```

Guarda la salida en `k6/results/api-all-endpoints.txt`:

```powershell
k6 run k6/api-all-endpoints.js 2>&1 | Tee-Object -FilePath k6\results\api-all-endpoints.txt
```

---

## PASO 6 — Evidencias

Carpeta: `docs/evidencias-item6/`

1. `k6 version`
2. `npm run k6:verify` → 100 %
3. Captura o archivo de `k6 run k6/api-all-endpoints.js` (checks en verde)

---

## Texto corto para Word

> Se implementaron pruebas de carga con **k6** sobre la API Happy Jump. El script `api-all-endpoints.js` ejecuta las **16 operaciones** definidas en el servidor (health, auth, reservas cancha/salones, reportes y PIN). Se verificó cobertura con `npm run k6:verify` al 100 % y se adjunta la salida de la ejecución smoke.

---

## Rutas cubiertas

| Método | Ruta |
|--------|------|
| GET | `/health`, `/hello`, `/sumar` |
| POST | `/auth/login`, `/auth/logout` |
| PUT | `/usuarios/:id/pin` |
| GET/POST | `/reservas-cancha` |
| PUT | `/reservas-cancha/:id/cobrar-saldo`, `.../cancelar` |
| GET/POST | `/reservas-salones` |
| PUT | `/reservas-salones/:id/cobrar-saldo`, `.../cancelar` |
| GET | `/reportes`, `/reportes/cancelaciones` |

Cuando termines, di **「listo el 6」** → siguiente: **ítem 7 (eliminado lógico)** o **ítem 8 (Sonar/Snyk)**.
