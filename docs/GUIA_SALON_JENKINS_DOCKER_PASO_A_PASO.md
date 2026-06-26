# Guía salón — Jenkins + Docker (Happy Jump)

Usa esta guía en la **PC del salón** (o cualquier PC con Docker). Sigue los pasos **en orden**.

---

## Antes de empezar (checklist)

| Requisito | Cómo comprobarlo |
|-----------|------------------|
| Windows 10/11 | |
| Docker Desktop instalado | Menú Inicio → "Docker Desktop" |
| Git instalado | `git --version` en PowerShell |
| Internet | Para descargar imágenes Docker |
| ~15 GB libres en disco | Para Docker + imágenes |
| Repo del proyecto | Clonado en disco local |

---

## PARTE 1 — Preparar la PC (una vez)

### Paso 1.1 — Instalar Docker Desktop (si no está)

1. Descarga: https://www.docker.com/products/docker-desktop/
2. Instala y **reinicia la PC**
3. Abre **Docker Desktop**
4. Acepta términos → espera **Engine running** (icono verde abajo)

### Paso 1.2 — Clonar el proyecto

Abre **PowerShell** y ejecuta (cambia la ruta si el profesor usa otra unidad):

```powershell
cd D:\
git clone https://github.com/Randy236/apphappy.git apphappy-full
cd D:\apphappy-full
```

Si ya tienes USB o carpeta copiada, solo:

```powershell
cd D:\apphappy-full
```

### Paso 1.3 — Crear `local.properties` (solo si vas a compilar Android en Jenkins)

Si el pipeline compila Android, crea el archivo:

`D:\apphappy-full\local.properties`

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

(Sustituye `TU_USUARIO` por tu usuario de Windows en el salón.)

---

## PARTE 2 — Levantar Jenkins con Docker (cada sesión)

### Paso 2.1 — Abrir Docker Desktop

1. Inicia **Docker Desktop**
2. Espera **1–3 min** hasta que diga **Docker Desktop is running** / motor verde
3. **No cierres** Docker mientras uses Jenkins

### Paso 2.2 — Comprobar que Docker funciona

PowerShell:

```powershell
docker version
```

Debe mostrar **Client** y **Server** (dos bloques).  
Si solo Client o error 500 → reinicia Docker Desktop o la PC.

```powershell
docker run --rm hello-world
```

Debe decir **Hello from Docker**.

### Paso 2.3 — Iniciar Jenkins (modo fácil)

**Opción A — Doble clic (recomendada)**

1. Ve a `D:\apphappy-full`
2. Clic derecho en **`INICIAR-JENKINS.bat`**
3. **Ejecutar como administrador**
4. Espera el mensaje **Puerto 8081 ABIERTO**
5. Se abrirá el navegador en Jenkins

**Opción B — PowerShell admin**

```powershell
cd D:\apphappy-full
.\scripts\iniciar-jenkins.ps1
```

**Primera vez:** puede tardar **5–15 min** (descarga imagen Jenkins).

### Paso 2.4 — Abrir Jenkins en el navegador

URL: **http://localhost:8081**

Si no carga, espera 2 min y pulsa **F5**.

### Paso 2.5 — Contraseña de desbloqueo (solo primera vez)

PowerShell:

```powershell
docker exec happyjump-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Copia la contraseña → pégala en Jenkins → **Continue**.

### Paso 2.6 — Wizard inicial de Jenkins (solo primera vez)

1. **Install suggested plugins**
2. Crea usuario admin (usuario + contraseña — **anótalos**)
3. URL: deja `http://localhost:8081/` → **Save and Finish**

---

## PARTE 3 — Configurar Jenkins para Happy Jump (una vez por PC)

### Paso 3.1 — NodeJS 22

1. **Manage Jenkins** → **Tools**
2. **NodeJS** → Add NodeJS
3. Name: **`NodeJS 22`** (exacto)
4. ✓ Install automatically → versión **22.x**
5. **Save**

### Paso 3.2 — (Opcional) Stack completo con SonarQube

Si el profesor pide **Sonar + Jenkins** en Docker:

```powershell
cd D:\apphappy-full
.\scripts\levantar-ci-e.ps1
```

O:

```powershell
cd D:\apphappy-full\infra\docker
docker compose up -d --build
```

| Servicio | URL |
|----------|-----|
| Jenkins (stack completo) | http://localhost:8081 |
| SonarQube | http://localhost:9000 |

Sonar: `admin` / `admin` → genera token → credencial Jenkins **`sonar-token`**.

### Paso 3.3 — Crear job Pipeline

1. **New Item** → nombre: **`happy-jump`** → **Pipeline** → OK
2. **Pipeline**:
   - **Pipeline script from SCM** (si hay internet y GitHub)
     - Git URL: `https://github.com/Randy236/apphappy.git`
     - Branch: `main`
     - Script Path: **`Jenkinsfile`**
   - **O** **Pipeline script** → pega el contenido de `D:\apphappy-full\Jenkinsfile`
3. **Save**

---

## PARTE 4 — Ejecutar CI/CD (comprobar que funciona)

### Paso 4.1 — Build Now

1. Job **happy-jump** → **Build Now**
2. Clic en el build (#1, #2…) → **Console Output**
3. Espera (primera vez **20–40 min** si compila Android)

### Paso 4.2 — Etapas que debe pasar

```
Checkout → Install Dependencies → Test → Sonar → Build → Deploy
```

Build **azul** = éxito.

### Paso 4.3 — Verificar deploy

**Modo simple (solo Jenkins):** el job con `Jenkinsfile` completo genera `dist/` en el workspace.

**Modo stack completo:**

```powershell
dir D:\apphappy-full\dist
dir .\deploy-runtime\api\current
dir .\deploy-runtime\app\current
```

Debes ver:
- `happyjump-api.tar.gz`
- `happyjump-app-release.apk`

---

## PARTE 5 — API para pruebas (Postman / app)

En otra ventana PowerShell:

```powershell
cd D:\apphappy-full\server
npm install
npm start
```

API: **http://localhost:3000/health**

---

## PARTE 6 — Evidencias para el profesor

Guarda capturas en `docs\evidencias-jenkins\`:

| # | Captura |
|---|---------|
| 1 | Docker Desktop — Engine running |
| 2 | `docker ps` con contenedor jenkins |
| 3 | Jenkins — pipeline **SUCCESS** (consola) |
| 4 | Carpeta `dist/` o deploy |
| 5 | (Opcional) SonarQube http://localhost:9000 |

---

## PARTE 7 — Al terminar la clase

```powershell
cd D:\apphappy-full\infra\docker
docker compose down
```

O solo Jenkins simple:

```powershell
cd D:\apphappy-full\infra\docker
docker compose -f docker-compose.jenkins-only.yml down
```

Puedes cerrar Docker Desktop.

---

## Problemas frecuentes

| Problema | Solución |
|----------|----------|
| `ERR_CONNECTION_REFUSED` en 8081 | Docker no está corriendo → Paso 2.1–2.3 |
| `500 Internal Server Error` en docker | Reinicia PC → abre Docker Desktop |
| `NodeJS 22` not found | Nombre exacto en Tools (Paso 3.1) |
| Build Android muy lento | Normal la 1ª vez; usa PC con buena RAM |
| Puerto 8081 ocupado | `docker ps` → para otro contenedor o cambia puerto en yml |

---

## Resumen en 6 líneas (para el salón)

```
1. Docker Desktop verde
2. cd D:\apphappy-full
3. INICIAR-JENKINS.bat (como administrador)
4. http://localhost:8081 → wizard + usuario
5. Job happy-jump → Build Now
6. Captura SUCCESS + dist/
```

---

## Archivos de ayuda en el repo

| Archivo |
|---------|
| `INICIAR-JENKINS.bat` |
| `scripts\iniciar-jenkins.ps1` |
| `scripts\reparar-docker-jenkins.ps1` |
| `docs\GUIA_JENKINS_DOCKER_PASO_A_PASO.md` |
| `docs\GUIA_JENKINS_CI_CD.md` |
| `Jenkinsfile` |
