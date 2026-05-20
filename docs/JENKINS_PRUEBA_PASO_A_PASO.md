# Probar Jenkins antes del push — Happy Jump

Orden recomendado: **preflight local** → **Docker (Sonar + Jenkins)** → **primer build en Jenkins**.

---

## A. Preflight (sin abrir Jenkins)

Simula el `Jenkinsfile` en tu PC:

```powershell
cd D:\apphappy
.\scripts\jenkins-preflight.ps1
```

| Etapa | Qué valida |
|-------|------------|
| 1 | `npm run test:ci` en `server/` |
| 2 | Gradle unit tests + JaCoCo (Docker Android) |
| 3 | Sonar (solo si `$env:SONAR_TOKEN` está definido) |
| 4 | `ci/build-production.sh` → carpeta `dist/` |
| 5 | `ci/deploy-server.sh` → `%USERPROFILE%\servers\happyjump\` |

Si falla **bash**: instala [Git for Windows](https://git-scm.com/) o el script usará Docker para ejecutar los `.sh`.

Versión corta (solo tests, sin build/deploy):

```powershell
.\scripts\ci-local.ps1
```

---

## B. Levantar SonarQube + Jenkins (Docker)

```powershell
cd D:\apphappy\infra\docker
docker compose build jenkins
docker compose up -d
docker compose ps
```

Espera 2–3 min (SonarQube la primera vez).

| URL | Uso |
|-----|-----|
| http://localhost:9000 | SonarQube — `admin` / `admin` (cambiar contraseña) |
| http://localhost:8081 | Jenkins |

### Si Docker falla con `input/output error`

1. Cierra apps que usen mucho disco.  
2. **Docker Desktop → Troubleshoot → Restart** (o *Clean / Purge data* si sigue corrupto).  
3. Vuelve a ejecutar `docker compose up -d`.

---

## C. Configuración única de Jenkins

### C.1 Contraseña inicial

```powershell
docker ps --format "{{.Names}}" | Select-String jenkins
docker exec <nombre-contenedor-jenkins> cat /var/jenkins_home/secrets/initialAdminPassword
```

Abre http://localhost:8081 → pega la contraseña → **Install suggested plugins** → crea usuario admin.

### C.2 Tool NodeJS

**Manage Jenkins → Tools → NodeJS installations**

- Name: **`NodeJS 22`** (exacto, como en el `Jenkinsfile`)
- Marca **Install automatically** → versión 22.x

### C.3 SonarQube server

**Manage Jenkins → System → SonarQube servers**

| Campo | Valor |
|-------|--------|
| Name | `sonarqube` |
| Server URL | `http://sonarqube:9000` |

### C.4 Credencial Sonar

**Manage Jenkins → Credentials → System → Global**

| ID | Tipo | Valor |
|----|------|--------|
| `sonar-token` | Secret text | Token de SonarQube (My Account → Security → Generate Token) |

En SonarQube crea proyecto **`happyjump-local`** (o deja que el primer análisis lo registre).

### C.5 Job Pipeline

1. **New Item** → `happy-jump` → **Pipeline**  
2. **Pipeline script from SCM**  
3. Git → URL de tu repo → rama `main` o `develop`  
4. Script Path: **`Jenkinsfile`**  

**Alternativa sin push (probar ya):**

- Mismo job → **Pipeline script** → pega el contenido de `Jenkinsfile` del disco  
- O **Pipeline script from SCM** apuntando a ruta local si tienes el plugin (menos común).

Para probar **antes de subir a GitHub**, en el job marca **This project is parameterized** no hace falta; usa copia del `Jenkinsfile` en el job o sube a una rama de prueba.

### C.6 Permisos Docker dentro de Jenkins

El contenedor Jenkins debe ver el socket Docker (ya está en `docker-compose.yml`). Comprueba en **Script Console** o en un build que `docker ps` funcione en un stage de prueba.

---

## D. Primer build

1. **Build Now**  
2. Consola: deben pasar **Checkout → Install → Test (API + Android) → Sonar → Build → Deploy**  
3. Comprueba deploy:

```powershell
dir $env:USERPROFILE\servers\happyjump\api\current
dir $env:USERPROFILE\servers\happyjump\app\current
Get-Content $env:USERPROFILE\servers\happyjump\logs\deploy.log -Tail 20
```

> Con el `docker-compose` actual, el deploy queda en **`D:\apphappy\deploy-runtime\`** (api/current, app/current, logs/deploy.log).

---

## E. Checklist rápido

- [ ] `.\scripts\jenkins-preflight.ps1` termina en verde  
- [ ] `docker compose ps` muestra `sonarqube` y `jenkins` healthy/up  
- [ ] http://localhost:8081 accesible  
- [ ] Credencial `sonar-token` creada  
- [ ] Tool `NodeJS 22` configurado  
- [ ] Sonar server `sonarqube` → `http://sonarqube:9000`  
- [ ] Job `happy-jump` build **SUCCESS**  
- [ ] Captura de pantalla del pipeline para entrega  

---

## F. Después de Jenkins OK

Ahí sí conviene `git push` y revisar en GitHub Actions el workflow **API Tests**.

Guía completa: [`GUIA_JENKINS_CI_CD.md`](GUIA_JENKINS_CI_CD.md)
