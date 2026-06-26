# Ítem 2 — CMMI + Scrum + Jira (áreas TS, VER, IP)

## Qué pide el profesor

1. Usar **Scrum** (sprints, backlog, tablero en Jira).
2. Relacionar el trabajo con **CMMI** (procesos de calidad — nivel 2).
3. Organizar el trabajo en **3 áreas**:
   - **TS** — Tecnología / solución técnica (código, API, app, Swagger, build).
   - **VER** — Verificación (pruebas, Postman, k6, cobertura, Sonar, Snyk).
   - **IP** — Implementación de procesos (Jira, ceremonias Scrum, CMMI, GitHub colaborativo, Jenkins como proceso).

**Entrega típica:** capturas de Jira + documento `SCRUM_CMMI_HAPPY_JUMP.md` + evidencias en carpeta.

---

## Archivos que ya tienes en el repo

| Archivo | Uso |
|---------|-----|
| `docs/SCRUM_CMMI_HAPPY_JUMP.md` | Informe Scrum + CMMI para Word/PDF |
| `docs/jira-import/happy-jump-backlog-areas-ts-ver-ip.csv` | Importar issues con columna **Area** |
| `docs/scrum/plantilla-sprint-planning.md` | Acta planning |
| `docs/scrum/daily-log.md` | Daily Scrum |
| `docs/scrum/plantilla-retrospectiva.md` | Retrospectiva |
| `docs/scrum/MATRIZ_SCRUM_CMMI.md` | Tabla Scrum ↔ CMMI |

---

## PASO 1 — Crear cuenta y proyecto Jira (10 min)

1. Entra a https://www.atlassian.com/software/jira/free  
2. Crea cuenta (o inicia sesión con Google).  
3. **Create project** → plantilla **Scrum**.  
4. Nombre: **Happy Jump**  
5. Clave del proyecto: **HJ** (las tareas serán HJ-1, HJ-2, …)  
6. Finish.

---

## PASO 2 — Importar el backlog con áreas TS / VER / IP (15 min)

1. En Jira: **⋯** (menú proyecto) → **Import** o **External import** → **CSV**.  
2. Sube el archivo:

```
D:\apphappy-full\docs\jira-import\happy-jump-backlog-areas-ts-ver-ip.csv
```

3. Mapea columnas:

| Columna CSV | Campo Jira |
|-------------|------------|
| Summary | Summary |
| Issue Type | Issue Type |
| Description | Description |
| Priority | Priority |
| Labels | Labels |
| Epic Name | Epic Name |
| Sprint | Sprint (si Jira lo pide al importar) |
| **Area** | **Labels** (añade también como label `area-TS`, etc.) |

4. Si **Area** no se importa sola:
   - Después del import, edita cada issue y añade label: `area-TS`, `area-VER` o `area-IP` según la columna Area del CSV.

5. Verifica que tengas **épocas** (Epics) y **stories/tasks**.

---

## PASO 3 — Crear las 3 áreas visibles en Jira (20 min)

El profesor debe **ver** TS, VER e IP. Tres formas (usa al menos **2**):

### Opción A — Labels (más fácil)

Ya vienen en el CSV: `area-TS`, `area-VER`, `area-IP`.

1. **Filters** → **Create filter**  
   - Nombre: `Happy Jump - TS`  
   - JQL: `project = HJ AND labels = area-TS`  
2. Repite:
   - `Happy Jump - VER` → `labels = area-VER`
   - `Happy Jump - IP` → `labels = area-IP`

### Opción B — Componentes (muy visible)

1. **Project settings** → **Components**  
2. Crea 3 componentes:
   - **TS** — Solución técnica  
   - **VER** — Verificación  
   - **IP** — Procesos e implementación  
3. Asigna cada issue al componente correcto (edición masiva si hace falta).

### Opción C — Tableros / Quick filters en el Board

1. Abre **Backlog** o **Board**.  
2. **Board settings** → **Quick filters**  
3. Añade:
   - `TS` → `labels = area-TS`
   - `VER` → `labels = area-VER`
   - `IP` → `labels = area-IP`

**Captura para el profesor:** tablero con los 3 filtros TS / VER / IP visibles.

---

## PASO 4 — Configurar Sprints Scrum (10 min)

1. **Backlog** → **Create sprint**  
2. Crea 3 sprints (2 semanas c/u o como pida el curso):
   - **Sprint 1** — Fundamentos (login, cancha, Jira IP)  
   - **Sprint 2** — Negocio (salones, reportes, pruebas VER)  
   - **Sprint 3** — Calidad y cierre (k6, Sonar, documentación)  
3. Arrastra issues del CSV a cada sprint según columna **Sprint** del CSV.

---

## PASO 5 — Etiquetas CMMI (opcional pero recomendado) (10 min)

En **Labels** del proyecto, usa también (ya en el CSV):

`cmmi-reqm` `cmmi-pp` `cmmi-pmc` `cmmi-ppqa` `cmmi-cm` `cmmi-ma`

Así enlazas Scrum con CMMI (ver tabla en `SCRUM_CMMI_HAPPY_JUMP.md` sección 3).

---

## PASO 6 — Ceremonias Scrum con evidencia (30 min)

Completa **al menos** esto (aunque sea retrospectivo para el curso):

| Ceremonia | Qué hacer | Archivo / captura |
|-----------|-----------|-------------------|
| **Sprint Planning** | Rellena plantilla + captura backlog/sprint | `docs/scrum/plantilla-sprint-planning.md` |
| **Daily** | 5–10 líneas (ayer/hoy/bloqueo) | `docs/scrum/daily-log.md` |
| **Sprint Review** | 3 fotos: app, Swagger, Postman | `docs/evidencias-scrum-cmmi/` |
| **Retrospectiva** | Qué bien / qué mejorar | `docs/scrum/plantilla-retrospectiva.md` |

Mueve issues en Jira: **To Do → In Progress → Done** según lo que ya hiciste.

---

## PASO 7 — Documento CMMI + Scrum para el profesor (20 min)

1. Abre `docs/SCRUM_CMMI_HAPPY_JUMP.md`  
2. Copia a **Word**  
3. Añade al inicio una tabla **Áreas de trabajo**:

| Área | Significado | Issues Jira (filtro) |
|------|-------------|----------------------|
| **TS** | Desarrollo técnico app + API | `labels = area-TS` |
| **VER** | Pruebas y calidad | `labels = area-VER` |
| **IP** | Procesos Scrum, Jira, CMMI, CI colaborativo | `labels = area-IP` |

4. Inserta **6–8 capturas** de Jira (backlog, board, filtros TS/VER/IP, burndown si existe).  
5. Guarda PDF: `INFORME_SCRUM_CMMI_HAPPY_JUMP.pdf`

---

## PASO 8 — Carpeta de evidencias (10 min)

Crea y llena:

```
D:\apphappy-full\docs\evidencias-scrum-cmmi\
```

| Captura | Nombre sugerido |
|---------|-----------------|
| Backlog con epics | `01-backlog.png` |
| Board con columnas | `02-board.png` |
| Filtro **TS** activo | `03-area-TS.png` |
| Filtro **VER** activo | `04-area-VER.png` |
| Filtro **IP** activo | `05-area-IP.png` |
| Sprint 1 o burndown | `06-sprint-burndown.png` |
| Issue en Done con label area-TS | `07-issue-done.png` |

---

## Qué va en cada área (referencia rápida)

| Área | Ejemplos en Happy Jump |
|------|------------------------|
| **TS** | Login, reservas cancha/salones, pantallas Compose, Swagger código, Jenkinsfile |
| **VER** | Tests API/Android, Postman, k6, SonarCloud, Snyk, casos CP-xxx |
| **IP** | Config Jira, Planning/Daily/Review/Retro, doc CMMI, PR reviews GitHub |

---

## Checklist ítem 2

```
[ ] Proyecto Jira HJ creado (Scrum)
[ ] CSV importado con labels area-TS, area-VER, area-IP
[ ] 3 filtros o componentes TS / VER / IP visibles
[ ] 3 sprints con issues asignados
[ ] Al menos 5 issues en Done
[ ] daily-log.md y planning/retro completados
[ ] PDF SCRUM_CMMI + capturas en evidencias-scrum-cmmi/
```

---

## Texto para el informe (copiar)

> El proyecto Happy Jump se gestiona con **Scrum** en Jira (proyecto HJ, tres sprints). El trabajo se organiza en tres áreas: **TS** (implementación técnica Android y API), **VER** (verificación mediante pruebas unitarias, integración Postman, k6 y análisis Sonar/Snyk) e **IP** (implementación de procesos: ceremonias Scrum, trazabilidad CMMI nivel 2 y colaboración en GitHub). Las evidencias incluyen capturas del tablero, filtros por área y documentación en `SCRUM_CMMI_HAPPY_JUMP.md`.

---

Cuando termines, di **"listo el 2"** y pasamos al **ítem 3 (2 reviews en GitHub)**.
