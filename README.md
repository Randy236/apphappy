# Happy Jump App

Aplicacion movil Android para gestion de reservas de cancha y salones, con roles de trabajador y administrador.

## Calidad (CI)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Randy236_apphappy&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Randy236_apphappy)

En GitHub Actions: **API Tests** (`server/`, `npm run test:ci`), **SonarCloud** (Android + JaCoCo) y **Snyk** en dependencias. Secretos: `SONAR_TOKEN` y `SNYK_TOKEN` (Sonar/Snyk). Detalle: [`docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md`](docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md).

## Stack

- Android: Kotlin + Jetpack Compose
- API: Node.js + Express
- Base de datos: MySQL

## Modulos principales

- Login por nombre + PIN
- Cancha: reservas por horario, estados y detalle
- Salones: reservas y disponibilidad (admin en solo lectura)
- Reportes: grafico de ingresos por area y resumen por periodo
- Perfil: cambio de PIN, historial y cierre de sesion
- Sesion unica por usuario (bloquea login en otro dispositivo activo)

## Requisitos

- JDK 11+
- Android SDK
- Node.js 18+
- MySQL 8+

## Configuracion rapida

### 1) Base de datos

Ejecutar:

- `server/schema.sql`

Si tu base ya existia antes de la sesion unica, ejecutar tambien:

- `server/migrations/001_usuarios_active_token.sql`

### 2) Backend

En `server/`:

```bash
npm install
npm start
```

API por defecto: `http://0.0.0.0:3000`

### 3) App Android

Producción (default en Gradle):

```properties
happyJump.api.baseUrl=https://happyjump.sorbits.site/
```

Definir en `local.properties` si necesitas otro entorno.

Compilar/instalar:

```bash
./gradlew :app:installDebug
```

## Producción

| Recurso | URL |
|---------|-----|
| API | https://happyjump.sorbits.site/ |
| Swagger | https://happyjump.sorbits.site/swagger-ui/ |
| Health | https://happyjump.sorbits.site/health |

## Credenciales demo (seed)

- Administrador: `Admin` / PIN `1234`
- Trabajador: `Juan Perez` / PIN `1234`

## Flujo sugerido para avances en GitHub

```bash
git add .
git commit -m "feat: descripcion corta del avance"
git push
```

Flujo por ramas recomendado:

```bash
git checkout develop
# trabajas cambios
git add .
git commit -m "feat: avance"
git push -u origin develop
```

Cuando el avance este estable:

```bash
git checkout main
git merge develop
git push
```

Tipos recomendados de commit:

- `feat:` nueva funcionalidad
- `fix:` correccion
- `refactor:` mejora interna
- `test:` pruebas
- `docs:` documentacion

## CI/CD

### GitHub Actions (CI + CD producción)

| Workflow | Función |
|----------|---------|
| `api-tests.yml` | Tests unitarios API |
| `sonarcloud.yml` | Tests Android, JaCoCo, SonarCloud |
| `snyk.yml` | Análisis de dependencias |
| `deploy-prod.yml` | Tras tests en `main`: webhook HTTPS → `GET /health` |

Secrets de deploy (solo nombres): `HAPPYJUMP_DEPLOY_TOKEN`, `HAPPYJUMP_PUBLIC_URL` (opcional).

Flujo CD: **push a `main` → tests → POST `/internal/deploy` → verificación `/health`**.

### Jenkins (laboratorio local, opcional)

- Guía: [`docs/GUIA_JENKINS_CI_CD.md`](docs/GUIA_JENKINS_CI_CD.md)
- Build: `ci/build-production.sh` → `dist/` (API `.tar.gz` + APK)
- Deploy local del agente: `ci/deploy-server.sh` → directorio de deploy en el servidor CI
- Pipeline: `Jenkinsfile` (tests → Sonar → build → deploy local)

## Material para curso

- Checklist de despliegue y testing: `docs/DESPLIEGUE_Y_PRUEBAS_CHECKLIST.md`
- Evidencias Sonar / Snyk / unit tests: `docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md`
- Documentación CI/CD: `docs/GUIA_JENKINS_CI_CD.md`

