# Despliegue Happy Jump API en Railway
# https://railway.app

## Requisitos
- Cuenta en https://railway.app (GitHub login)
- Node.js en tu PC

## Opción A — Desde la web (más fácil, ~15 min)

### 1. Subir código a GitHub
Si aún no está: push de la carpeta `server/` o todo el repo a GitHub.

### 2. Crear proyecto en Railway
1. Entra a https://railway.app → **New Project**
2. **Deploy from GitHub repo** → elige `Randy236/apphappy`
3. En **Settings** del servicio → **Root Directory** = `server`
4. **Settings** → **Networking** → **Generate Domain** (te da la URL pública)

### 3. Agregar MySQL
1. En el mismo proyecto → **+ New** → **Database** → **MySQL**
2. Abre el servicio **API** → **Variables** → **Add Reference** → selecciona MySQL
   (Railway inyecta MYSQLHOST, MYSQLPORT, MYSQLUSER, MYSQLPASSWORD, MYSQLDATABASE)

### 4. Variables obligatorias (servicio API)
| Variable | Valor |
|----------|--------|
| `NODE_ENV` | `production` |
| `JWT_SECRET` | Una clave larga aleatoria (ej. `happyjump_prod_2026_secreto_xyz`) |

### 5. Deploy
Railway despliega solo. Al arrancar ejecuta:
- `npm run start:railway` → crea tablas + seed Admin/Rosisela PIN 1234

### 6. Probar URLs
```
https://TU-DOMINIO.up.railway.app/health
https://TU-DOMINIO.up.railway.app/swagger-ui/
```

### 7. App Android
En `local.properties`:
```properties
happyJump.api.baseUrl=https://TU-DOMINIO.up.railway.app/
```
Rebuild e instala la app.

---

## Opción B — CLI (PowerShell)

```powershell
npm install -g @railway/cli
cd D:\apphappy-full\server
railway login
railway init
railway add --database mysql
railway variables set NODE_ENV=production
railway variables set JWT_SECRET=tu_secreto_largo_aqui
railway up
railway domain
```

---

## Script automático (Windows)

```powershell
cd D:\apphappy-full
.\scripts\deploy-railway.ps1
```

---

## Usuarios demo (después del deploy)
| Usuario | PIN |
|---------|-----|
| Admin | 1234 |
| Rosisela | 1234 |

## Entregable 10 — texto para el profesor
```
API desplegada en Railway (servidor cloud):
  Health: https://[dominio]/health
  Swagger: https://[dominio]/swagger-ui/
  Repositorio: https://github.com/Randy236/apphappy
App Android: APK conectada a la URL de Railway
```
