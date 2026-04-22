# Happy Jump App

Aplicacion movil Android para gestion de reservas de cancha y salones, con roles de trabajador y administrador.

## Calidad (CI)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Randy236_apphappy&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Randy236_apphappy)

En GitHub Actions: análisis **SonarCloud** (tests + JaCoCo) y **Snyk** en dependencias. Requiere secretos `SONAR_TOKEN` y `SNYK_TOKEN`. Detalle y plantilla para evidencias académicas: [`docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md`](docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md).

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

Definir `happyJump.api.baseUrl` en `local.properties`:

- Emulador: `http://10.0.2.2:3000/`
- Celular fisico (misma Wi-Fi): `http://IP_DE_TU_PC:3000/`

Compilar/instalar:

```bash
./gradlew :app:installDebug
```

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

## Material para curso

- Checklist de despliegue y testing: `docs/DESPLIEGUE_Y_PRUEBAS_CHECKLIST.md`
- Entrega calidad / Sonar / Snyk / unit tests: `docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md`
- Checklist profesor (ítems 2–5): `docs/CHECKLIST_ENTREGA_PROFESOR.md`

