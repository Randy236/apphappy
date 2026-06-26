# MANUAL DE INTEGRACIÓN Y DESPLIEGUE CONTINUO (CI/CD)

**Proyecto:** Happy Jump  
**Repositorio:** https://github.com/Randy236/apphappy  
**Fecha:** __________________  
**Integrantes:** __________________  

---

## 1. Introducción

Este manual describe la estrategia de **Integración Continua (CI)** y **Despliegue Continuo (CD)** del sistema Happy Jump, compuesto por:

- **Cliente Android** (`app/`) — Kotlin, Jetpack Compose, Retrofit  
- **API REST** (`server/`) — Node.js, Express, MySQL, JWT  

El objetivo del CI/CD es ejecutar pruebas y análisis de calidad en cada cambio de código y producir artefactos listos para despliegue de forma repetible.

---

## 2. Modelo híbrido Back / Front

Happy Jump es **una sola aplicación de negocio** con dos capas desacopladas:

| Capa | Carpeta | Tecnología | Artefacto CI/CD |
|------|---------|------------|-----------------|
| **Back (API)** | `server/` | Node.js, Express, MySQL | `dist/happyjump-api.tar.gz` → `api/current/` |
| **Front (cliente)** | `app/` | Kotlin, Compose, Retrofit | `dist/happyjump-app-release.apk` → `app/current/` |

La UI **no** accede a MySQL directamente: consume la API por HTTP (`BuildConfig.API_BASE_URL`). En el pipeline se **prueban y empaquetan por separado**, pero se despliegan con el mismo job Jenkins o la misma estrategia de scripts.

---

## 3. Arquitectura CI/CD

```
┌─────────────┐     push/PR      ┌──────────────────┐
│  Desarrollo │ ───────────────► │ GitHub Actions   │
│  (Git)      │                  │ + Jenkins (opt.) │
└─────────────┘                  └────────┬─────────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    ▼                     ▼                     ▼
              Tests API            Tests Android           Sonar / Snyk
              npm run test:ci      Gradle unit + JaCoCo     Calidad / deps
                    │                     │                     │
                    └─────────────────────┼─────────────────────┘
                                          ▼
                              ci/build-production.sh
                                          │
                    ┌─────────────────────┴─────────────────────┐
                    ▼                                           ▼
         dist/happyjump-api.tar.gz              dist/happyjump-app-release.apk
                    │                                           │
                    └─────────────────────┬─────────────────────┘
                                          ▼
                              ci/deploy-server.sh
                                          │
                                          ▼
                         directorio de deploy del agente CI (api/current, app/current)
```

---

## 4. GitHub Actions (CI en la nube)

Ubicación: `.github/workflows/`

| Workflow | Disparador | Qué hace |
|----------|------------|----------|
| **API Tests** (`api-tests.yml`) | push/PR en `main`, `develop` | `npm ci` + `npm run test:ci` en `server/` |
| **Deploy API (prod)** (`deploy-prod.yml`) | push en `main` (server/) | Tests API + webhook HTTPS + `GET /health` |
| **SonarCloud** (`sonarcloud.yml`) | push/PR + manual | Tests Android, JaCoCo, análisis Sonar |
| **Snyk Security** (`snyk.yml`) | push/PR + manual | Escaneo de vulnerabilidades en dependencias Gradle |

### 4.1 API Tests (evidencia principal)

Comando equivalente en local:

```powershell
cd server
npm ci
npm run test:ci
```

**Secretos GitHub (Actions):**

| Secreto | Uso |
|---------|-----|
| `SONAR_TOKEN` | Publicar análisis a SonarCloud |
| `SNYK_TOKEN` | Escaneo Snyk |
| `HAPPYJUMP_DEPLOY_TOKEN` | Webhook CD producción (contactar ops) |
| `HAPPYJUMP_PUBLIC_URL` | URL pública API (opcional; default prod) |

**Evidencia:** captura de https://github.com/Randy236/apphappy/actions — workflow **API Tests** en verde.

---

## 5. Jenkins y Docker (pipeline declarativo — laboratorio)

Resumen de los **cinco bloques** del manual de configuración de herramientas:

| Bloque | Contenido | Archivo / ubicación |
|--------|-----------|---------------------|
| **1. Configuración Jenkins** | Plugins, NodeJS 22, Sonar server, credenciales | `infra/jenkins/`, UI Jenkins |
| **2. Jenkinsfile** | Pipeline declarativo (etapas) | `Jenkinsfile` (raíz del repo) |
| **3. Build** | Empaquetar API + APK | `ci/build-production.sh` |
| **4. Deploy** | Publicar en carpetas `current` + rollback | `ci/deploy-server.sh` |
| **5. Flujo resumido** | Orden de ejecución end-to-end | Sección 5.4 más abajo |

### 5.1 Configuración de Jenkins

- **Instalación:** Jenkins en Docker (`infra/docker/docker-compose.yml`) o instalación local.
- **Plugins:** Pipeline, Git, SonarQube Scanner, NodeJS, credentials-binding, docker-workflow (lista en `infra/jenkins/plugins.txt`).
- **Tools en Jenkins:** NodeJS **22**, Git, SonarQube Scanner.
- **Credenciales:**

| ID | Tipo | Uso |
|----|------|-----|
| `sonar-token` | Secret text | Análisis SonarQube local |
| `github-token` | Opcional | Solo si el job clona por URL sin SCM |
| `deploy-token` | Opcional | Validación en `ci/deploy-with-token.sh` |

- **SonarQube server en Jenkins:** nombre `sonarqube`, URL `http://sonarqube:9000`.

### 5.2 Jenkinsfile — pipeline

Archivo: **`Jenkinsfile`** en la raíz del repositorio.

| Etapa | Descripción |
|-------|-------------|
| **Checkout SCM** | Obtiene código del repositorio (rama `main`) |
| **Install Dependencies** | `npm ci` en `server/` |
| **Test** | Paralelo: API (`npm run test:ci`) + Android (Gradle en Docker) |
| **Sonar** | `./gradlew sonar` contra SonarQube local (Docker :9000) |
| **Build** | `./ci/build-production.sh` |
| **Deploy** | `./ci/deploy-server.sh` |

### 5.3 Infraestructura Docker

Antes de levantar el stack:

```powershell
cd infra/docker
docker compose up -d --build
```

| Servicio | URL | Datos |
|----------|-----|-------|
| SonarQube | http://localhost:9000 | Volumen Docker `sonarqube-data` |
| Jenkins | http://localhost:8081 | Volumen Docker `jenkins-home` |

Guía detallada: `docs/GUIA_JENKINS_CI_CD.md`

### 5.4 Flujo resumido

Secuencia que ejecuta Jenkins en cada **Build Now**:

| Paso | Acción | Back / Front |
|------|--------|--------------|
| 1 | Checkout del repositorio | Ambos (`server/` + `app/`) |
| 2 | Instalar dependencias (`npm ci` en `server/`) | Back |
| 3 | Ejecutar tests (`npm run test:ci` + Gradle unitarias/JaCoCo) | Back + Front |
| 4 | Analizar con SonarQube (`./gradlew sonar`) | Front (código Kotlin) |
| 5 | Construir artefactos (`ci/build-production.sh` → `dist/`) | Back (.tar.gz) + Front (.apk) |
| 6 | Deploy en agente CI (`ci/deploy-server.sh` → directorio de deploy) | `api/current` + `app/current` |
| 7 | CD producción (`deploy-prod.yml` → webhook → `/health`) | API en https://happyjump.sorbits.site/ |

Sin Tomcat ni ngrok: solo **Jenkins + scripts + Sonar** en Docker.

---

## 6. Scripts de build y deploy

### 6.1 `ci/build-production.sh`

- Empaqueta la API Node (`server/`) en **`dist/happyjump-api.tar.gz`** (sin `node_modules`).
- Compila APK release Android en **`dist/happyjump-app-release.apk`** (imagen Docker Android).

### 6.2 `ci/deploy-server.sh`

- Copia artefactos a **`$HAPPYJUMP_DEPLOY_BASE`** (directorio de deploy del agente CI).
- Estructura: `api/current/`, `app/current/`, `logs/deploy.log`.
- Soporta rollback mediante script auxiliar `ci/deploy-with-token.sh` (token opcional).

### 6.3 Preflight local (Windows)

Simula el pipeline sin abrir Jenkins:

```powershell
.\scripts\jenkins-preflight.ps1
```

---

## 7. Flujo de trabajo del desarrollador

1. Crear rama / commit en Git.  
2. Push a GitHub → se disparan workflows automáticamente.  
3. Revisar **Actions** (tests API deben pasar).  
4. Merge a `main` → Jenkins (si está configurado) puede ejecutar build + deploy.  
5. Verificar artefactos en `dist/` o en servidor de deploy.

---

## 8. Estado actual y pendientes

| Componente | Estado |
|------------|--------|
| Tests API en CI | Operativo (`api-tests.yml`) |
| Tests Android en CI | Configurado (`sonarcloud.yml`) |
| SonarCloud + cobertura | Pendiente (Automatic Analysis en SonarCloud) |
| Snyk | Configurado (requiere `SNYK_TOKEN`) |
| Jenkins local | Documentado; requiere Docker Desktop |

---

## 9. Conclusiones

Happy Jump cuenta con pipelines automatizados que validan la API en cada integración y preparan artefactos de despliegue mediante scripts versionados en el repositorio. GitHub Actions proporciona CI accesible sin infraestructura propia; Jenkins complementa el flujo con build, análisis Sonar local y deploy en entorno controlado.

---

## 10. Anexos

- Captura A: GitHub Actions — API Tests exitoso  
- Captura B: Estructura de carpetas `ci/` y `Jenkinsfile`  
- Captura C: (Opcional) Consola Jenkins — pipeline verde  
- Enlaces: repo GitHub, documentación en `docs/GUIA_JENKINS_CI_CD.md`
