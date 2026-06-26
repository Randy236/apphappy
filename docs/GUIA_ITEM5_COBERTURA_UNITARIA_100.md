# Ítem 5 — Cobertura de pruebas unitarias (100 %)

## Qué pide el profesor

**Cobertura del 100 %** en pruebas unitarias del proyecto.

En Happy Jump hay **dos capas**:

| Capa | Ubicación | Enfoque realista para el 100 % |
|------|-----------|--------------------------------|
| **API — reglas de negocio** | `server/src/domain/` | **100 %** con tests Node (`npm test`) |
| **Android — lógica pura** | `app/src/main/.../ui/util/` | Tests JVM + JaCoCo en paquete `util` |
| **Android — UI Compose** | Pantallas, ViewModels | Cobertura global baja; no es práctico 100 % sin semanas de UI tests |

La estrategia del repo: **demostrar 100 % en la lógica extraída y testeable** (domain API + util Android), y dejar evidencia con comandos y capturas.

---

## Parte A — API (server)

### PASO 1 — Ejecutar todos los tests (1 min)

```powershell
cd D:\apphappy-full\server
npm install
npm test
```

Debe terminar sin fallos (varios archivos en `server/test/`).

### PASO 2 — Verificar que cada módulo `domain` tiene test (1 min)

```powershell
npm run coverage:verify
```

Mensaje esperado: todos los módulos de `src/domain/` con test y `npm test OK`.

### PASO 3 — Cobertura de líneas (Node, opcional informe)

```powershell
npm run test:coverage
```

Revisa el resumen al final (cobertura sobre `src/domain/**`). Objetivo: **100 % líneas** en esos archivos.

### Módulos cubiertos

| Módulo | Regla de negocio |
|--------|------------------|
| `canchaEstado.js` | Estado `con_adelanto` / `ocupado` |
| `canchaReglas.js` | Horarios cancha, duración, traslapes |
| `timeUtil.js` | Conversión hora → minutos |
| `reportesFiltro.js` | Rangos diario / semanal / mensual |
| `salonReglas.js` | Traslape salones, salón válido |
| `k6Util.js` | Validación `/sumar` |

La lógica vive en `domain/`; `index.js` solo orquesta Express y MySQL.

---

## Parte B — Android (app)

### PASO 1 — Tests unitarios JVM

```powershell
cd D:\apphappy-full
.\gradlew.bat :app:testDebugUnitTest
```

Tests en `app/src/test/java/com/example/happyj/ui/util/`:

- `TimeUtilTest`
- `CanchaGrupoTurnoTest`
- `CanchaDisponibilidadCalendarioTest`

### PASO 2 — Reporte JaCoCo local

```powershell
.\scripts\run-coverage-local.ps1
```

Abre el HTML generado (ruta que imprime el script). Para el informe:

1. Captura **cobertura global** del módulo `app`.
2. Captura **cobertura del paquete** `com.example.happyj.ui.util` (debe ser alta; meta docente: lógica de negocio alineada con la API).

Si el profesor exige solo “lógica de negocio”, argumenta con esta separación (domain + util = reglas; UI = integración manual / E2E).

---

## Parte C — Evidencias para el documento

Carpeta sugerida: `docs/evidencias-item5/`

| # | Captura / archivo |
|---|-------------------|
| 1 | Salida de `npm test` (todo verde) |
| 2 | Salida de `npm run coverage:verify` |
| 3 | Resumen de `npm run test:coverage` (domain) |
| 4 | `gradlew :app:testDebugUnitTest` BUILD SUCCESSFUL |
| 5 | JaCoCo HTML — paquete `ui.util` |
| 6 | Tabla en Word: módulo → archivo test → % |

Texto sugerido para el informe:

> Se extrajo la lógica de negocio de `index.js` a `server/src/domain/` con pruebas unitarias Node. En Android, las reglas equivalentes están en `ui.util` con tests JVM. La cobertura al 100 % se aplica a esas capas; las pantallas Compose requieren pruebas instrumentadas y quedan fuera del alcance de unit tests puros.

---

## Relación con otros ítems

| Ítem | Conexión |
|------|----------|
| **4 Swagger** | Misma API; endpoints documentados |
| **8 SonarCloud / Snyk** | Suben el % cuando Automatic Analysis esté desactivado y exista `SONAR_TOKEN` |
| **6 k6** | Carga HTTP; no sustituye unit tests |

---

## Comandos rápidos (copiar al informe)

```powershell
cd D:\apphappy-full\server
npm test
npm run coverage:verify
npm run test:coverage

cd D:\apphappy-full
.\gradlew.bat :app:testDebugUnitTest
.\scripts\run-coverage-local.ps1
```

---

## Checklist ítem 5

- [ ] `npm test` sin errores
- [ ] `npm run coverage:verify` OK
- [ ] `npm run test:coverage` revisado (domain)
- [ ] `testDebugUnitTest` OK en Android
- [ ] Capturas en `docs/evidencias-item5/`
- [ ] Párrafo en documento de avance (ítem 1)

Cuando termines, di **「listo el 5」** y seguimos con **ítem 6 (k6)** o **ítem 7 (eliminado lógico)** según el orden del checklist.
