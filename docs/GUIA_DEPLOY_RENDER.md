# Despliegue Happy Jump en Render (GRATIS)

Render hospeda la **API Node.js** gratis. MySQL no incluye Render free → usamos **db4free.net** (MySQL gratis para proyectos académicos).

---

## PARTE A — MySQL gratis (db4free.net) ~5 min

1. Abre https://www.db4free.net/signup.php
2. Regístrate (email + contraseña)
3. Crea base de datos:
   - **Database name:** `happy_jump` (o el nombre que te asignen)
   - Anota: **Host**, **Usuario**, **Contraseña**, **Nombre BD**

4. Entra a **phpMyAdmin** desde db4free (enlace en su panel)
5. Opcional: no hace falta importar SQL manualmente — la API crea tablas al arrancar

**Datos típicos db4free:**
| Campo | Ejemplo |
|-------|---------|
| DB_HOST | `db4free.net` |
| DB_PORT | `3306` |
| DB_USER | `tu_usuario_db4free` |
| DB_PASSWORD | `tu_contraseña` |
| DB_NAME | `happy_jump` |

---

## PARTE B — Render (API en la nube) ~10 min

### 1. Cuenta Render
- https://render.com → **Get Started** → login con **GitHub**
- Plan **Free** (no pide tarjeta para Web Service básico)

### 2. Conectar repositorio
1. **Dashboard** → **New +** → **Blueprint**
2. Conecta GitHub → repo **`Randy236/apphappy`**
3. Render detecta `render.yaml` en la raíz → **Apply**

   **O manualmente (si Blueprint no aparece):**
   - **New +** → **Web Service**
   - Repo `apphappy`
   - **Root Directory:** `server`
   - **Build Command:** `npm install`
   - **Start Command:** `npm run start:cloud`
   - **Plan:** Free

### 3. Variables de entorno (Environment)

En el servicio **happy-jump-api** → **Environment**:

| Key | Value |
|-----|--------|
| `NODE_ENV` | `production` |
| `JWT_SECRET` | `HappyJumpRender2026SecretoLargoXYZ` (cambia por uno tuyo) |
| `DB_HOST` | `db4free.net` |
| `DB_PORT` | `3306` |
| `DB_USER` | *(tu usuario db4free)* |
| `DB_PASSWORD` | *(tu contraseña db4free)* |
| `DB_NAME` | *(nombre de tu BD)* |
| `DB_SSL` | `false` |

Guarda → Render redeploya solo.

### 4. Obtener URL pública

**Settings** → tu URL será algo como:
```
https://happy-jump-api.onrender.com
```

### 5. Probar (espera 2–5 min el primer deploy)

```
https://happy-jump-api.onrender.com/health
https://happy-jump-api.onrender.com/swagger-ui/
```

**Nota plan Free:** si no hay visitas 15 min, el servicio "duerme". La **primera petición** tarda ~30–60 s en despertar. Normal en plan gratis.

---

## PARTE C — App Android

En `local.properties`:
```properties
happyJump.api.baseUrl=https://happy-jump-api.onrender.com/
```
*(Usa TU URL de Render, con `/` al final)*

Android Studio → **Rebuild Project** → instalar en celular.

Usuarios demo (se crean solos al primer arranque):
| Usuario | PIN |
|---------|-----|
| Admin | 1234 |
| Rosisela | 1234 |

---

## Entregable 10 — texto para el profesor

```
Happy Jump — Desplegado en servidor cloud

Plataforma:  Render.com (plan Free)
API URL:     https://[tu-app].onrender.com/health
Swagger:     https://[tu-app].onrender.com/swagger-ui/
Base datos:  MySQL (db4free.net)
Repositorio: https://github.com/Randy236/apphappy
Cliente:     App Android APK → API HTTPS en Render
Estado:      Login, Cancha, Salones, Reportes, Perfil operativos
```

---

## Problemas frecuentes

| Error | Solución |
|-------|----------|
| Deploy failed — JWT | Agrega `JWT_SECRET` en Environment |
| ECONNREFUSED MySQL | Revisa DB_HOST, user, password en Render |
| db4free "too many connections" | Espera 5 min o usa otra BD |
| /health tarda mucho | Plan Free despertando — espera 60 s |
| App no conecta | URL con `https://` y `/` final en local.properties |

---

## Script ayuda local

```powershell
cd D:\apphappy-full
.\scripts\deploy-render.ps1
```
