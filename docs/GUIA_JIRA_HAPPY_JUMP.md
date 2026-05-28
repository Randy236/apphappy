# Guía Jira — Proyecto Happy Jump

Pasos para usar **Jira** como pide tu profesor, con el backlog ya armado para esta aplicación.

---

## 1. Crear cuenta y proyecto

1. Entra a **[https://www.atlassian.com/software/jira/free](https://www.atlassian.com/software/jira/free)** (plan gratuito para equipos pequeños).
2. Crea un sitio (ej. `tu-equipo.atlassian.net`).
3. **Create project** → plantilla **Scrum** (o **Kanban** si el profesor no exige sprints).
4. Nombre sugerido: **Happy Jump**
5. Clave del proyecto: **HJ** (las tareas serán `HJ-1`, `HJ-2`, …).

---

## 2. Importar el backlog (rápido)

En el repo tienes un CSV listo:

```
docs/jira-import/happy-jump-backlog.csv
```

**En Jira:**

1. Menú **≡** → **Jira settings** (o **Settings** del proyecto).
2. **System** → **External imports** → **CSV** (o en el proyecto: **⋯** → **Import**).
3. Sube `happy-jump-backlog.csv`.
4. Mapea columnas:
   - `Summary` → Summary
   - `Issue Type` → Issue Type
   - `Description` → Description
   - `Priority` → Priority
   - `Labels` → Labels
   - `Epic Name` → Epic Name (o Epic Link)
5. Importa.

Si no aparece **Epic Name**, importa primero las filas tipo **Epic**, luego el resto y asigna épicas a mano.

---

## 3. Estructura del tablero (lo que verá el profesor)

### Épicas (7)

| Épica | Contenido |
|-------|-----------|
| Autenticación y seguridad | Login, sesión única, PIN |
| Reservas de cancha | CRUD cancha |
| Reservas de salones | CRUD salones |
| Reportes administrador | Ingresos y cancelaciones |
| App Android (UI) | Pantallas Compose |
| Calidad CI/CD y pruebas | Jenkins, k6, Sonar, Swagger |
| Documentación y entrega | Checklists y guías |

### Tipos de issue

| Tipo | Uso en Happy Jump |
|------|-------------------|
| **Epic** | Módulo grande (ej. Reservas cancha) |
| **Story** | Funcionalidad para el usuario |
| **Task** | Técnico (CI, docs, migraciones) |
| **Bug** | Cuando encuentres un fallo |

### Estados típicos (Scrum)

`To Do` → `In Progress` → `In Review` → `Done`

Marca como **Done** lo que ya tienes hecho (login, API, Swagger, k6, etc.).

---

## 4. Sprints de ejemplo (3 entregas)

### Sprint 1 — Fundamentos (2 semanas)

| Issue | Estado sugerido |
|-------|-----------------|
| Esquema MySQL y migraciones | Done |
| Login con nombre y PIN | Done |
| Pantalla login Compose | Done |
| Listar / crear reserva cancha | Done |

**Objetivo del sprint:** API + app con login y cancha básica.

### Sprint 2 — Salones y reportes

| Issue | Estado sugerido |
|-------|-----------------|
| Reservas salones (API + UI) | Done / In Progress |
| Reportes admin | Done |
| Sesión única, cancelaciones | Done |

### Sprint 3 — Calidad y entrega

| Issue | Estado sugerido |
|-------|-----------------|
| Jenkins / GitHub Actions | Done |
| k6, Swagger | Done |
| Checklist entrega profesor | To Do |
| Configurar proyecto en Jira | In Progress |

En Jira: **Backlog** → selecciona issues → **Create sprint** → arrastra tarjetas.

---

## 5. Buenas prácticas (suelen pedir en clase)

### Descripción de una Story (plantilla)

```
Como [trabajador/admin]
Quiero [acción]
Para [beneficio]

Criterios de aceptación:
- [ ] ...
- [ ] ...

API: GET/POST ...
App: pantalla X
```

**Ejemplo — Crear reserva cancha:**

```
Como trabajador
Quiero registrar una reserva de cancha
Para ocupar un horario y cobrar adelanto

Criterios:
- [ ] Valida horario 8:00–22:00
- [ ] No permite traslapes
- [ ] Muestra estado ocupado / con adelanto
API: POST /reservas-cancha
```

### Vincular con GitHub (opcional)

1. Jira → **Settings** → **Apps** → **GitHub for Jira** (integración).
2. En cada commit: `HJ-12 feat: pantalla reportes admin`
3. La tarjeta **HJ-12** se actualiza sola.

### Evidencia para el profesor

Capturas de:

1. **Backlog** con épicas
2. **Board** (Sprint) con columnas To Do / Done
3. **Burndown chart** (Reports → Sprint burndown)
4. Una **Story** abierta con descripción y criterios

---

## 6. Tablero mínimo si no puedes importar CSV

Crea a mano **3 épicas** y **5 stories**:

| Clave | Título | Tipo |
|-------|--------|------|
| HJ-1 | Autenticación | Epic |
| HJ-2 | Reservas cancha y salones | Epic |
| HJ-3 | Calidad y entrega | Epic |
| HJ-4 | Login PIN | Story |
| HJ-5 | Reservas cancha | Story |
| HJ-6 | Reservas salones | Story |
| HJ-7 | Reportes admin | Story |
| HJ-8 | Pruebas k6 + Swagger | Task |

---

## 7. Enlace con tu repo

En la descripción del proyecto Jira pon:

- Repositorio: `https://github.com/Randy236/apphappy`
- Documentación: carpeta `docs/`
- API Swagger: `http://localhost:3000/swagger-ui/`

---

## 8. Checklist antes de mostrar al profesor

- [ ] Proyecto **Happy Jump** creado en Jira
- [ ] Al menos **3 épicas** y **10+ issues**
- [ ] **1 sprint** activo o cerrado con fechas
- [ ] Varios issues en **Done** (lo ya implementado)
- [ ] Stories con **criterios de aceptación**
- [ ] (Opcional) Commits con `HJ-XX` en el mensaje

---

## Archivos en este repo

| Archivo | Uso |
|---------|-----|
| `docs/jira-import/happy-jump-backlog.csv` | Importar backlog |
| `docs/GUIA_JIRA_HAPPY_JUMP.md` | Esta guía |

## Scrum + CMMI (si el profesor pide ambos)

Lee la guía completa: **[SCRUM_CMMI_HAPPY_JUMP.md](SCRUM_CMMI_HAPPY_JUMP.md)**

- Importa: `docs/jira-import/happy-jump-backlog-scrum-cmmi.csv` (incluye etiquetas `cmmi-reqm`, `cmmi-pp`, etc.)
- Matriz informe: `docs/scrum/MATRIZ_SCRUM_CMMI.md`
- Plantillas: `docs/scrum/plantilla-sprint-planning.md`, `daily-log.md`, `plantilla-retrospectiva.md`

Si tu profesor usa **plantilla específica** (campos obligatorios, diagrama Gantt en Jira, etc.), dime qué pide y lo adaptamos.
