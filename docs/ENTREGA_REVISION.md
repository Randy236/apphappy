# Entrega para revisión (proyecto Happy Jump / apphappy)

## Qué incluye este repositorio

- **App Android (Kotlin + Jetpack Compose)**: módulos de cancha, salones, admin, perfil, login con PIN, exportación donde aplique.
- **API Node.js + MySQL** (`server/`): autenticación JWT, reservas de cancha y salones, reportes, migraciones SQL.
- **Documentación en `docs/`**:
  - `REQUERIMIENTOS.csv` — requisitos funcionales.
  - `CASOS_DE_PRUEBA.csv` — casos de prueba.
  - `PLAN_DE_PRUEBAS.md` — plan de pruebas.
  - `DESPLIEGUE_Y_PRUEBAS_CHECKLIST.md` — despliegue y checklist (si está presente).

## Mejoras recientes (resumen)

1. **Cancha**: calendario mensual con semáforo (libre / parcial / lleno / pasado); agrupación de un solo turno largo («Otra hora» en varias franjas) en una fila y detalle unificado; corrección de fechas ISO desde API.
2. **Servidor**: listado `GET /reservas-cancha` incluye `duracion_minutos` para mostrar bien rangos como 17:00–18:30 (1 h 30 min) y no inferir mal 2 h.
3. **Raíz**: `package.json` con `npm start` que arranca la API en `server/`.

## Cómo revisa el profesor

1. Clonar el repo y abrir `docs/` (CSV/MD pueden abrirse en Excel, VS Code o GitHub).
2. Backend: `cd server`, `npm install`, configurar `.env` (no versionado), `npm start`.
3. Android: abrir carpeta en Android Studio, `local.properties` con SDK (no subir al repo).

## Rama sugerida

Trabajo integrado en **`develop`**; alinear con **`main`** según política del curso.
