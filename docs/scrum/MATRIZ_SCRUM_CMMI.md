# Matriz de trazabilidad Scrum ↔ CMMI — Happy Jump

Usa esta tabla en tu informe o presentación.

| Ceremonia / artefacto Scrum | Área proceso CMMI | Evidencia en el proyecto |
|----------------------------|-------------------|---------------------------|
| Product Backlog | REQM | Jira + `REQUERIMIENTOS.csv` |
| Sprint Planning | PP | `plantilla-sprint-planning.md` + sprint Jira |
| Sprint Backlog | PP | Tablero Jira (columnas) |
| Daily Scrum | PMC | `daily-log.md` |
| Desarrollo / Incremento | CM + VER | GitHub commits, tests |
| Sprint Review | VAL + PMC | Demo app, Swagger |
| Retrospective | Mejora (OPF lite) | `plantilla-retrospectiva.md` |
| Definition of Done | PPQA | Checklist en `SCRUM_CMMI_HAPPY_JUMP.md` |
| Burndown / velocity | MA + PMC | Reportes Jira |
| Plan de pruebas | PPQA + VER | `PLAN_MAESTRO_PRUEBAS_*.md` |
| Casos de prueba CP-xxx | VER | `CASOS_DE_PRUEBA.csv` |
| Sonar / Snyk | PPQA + MA | `EVIDENCIAS_CALIDAD_SONAR_SNYK.md` |
| k6 | MA | `k6/results/` |
| Swagger | REQM + VAL | `/swagger-ui/` |
| Jenkins / GitHub Actions | CM + PPQA | `Jenkinsfile`, workflows |

## Nivel de madurez declarado

| Nivel CMMI | ¿Aplica? | Justificación breve |
|------------|----------|---------------------|
| 1 Inicial | Superado | Hay procesos documentados |
| **2 Managed** | **Sí — objetivo** | Planificación, seguimiento, calidad, CM, medición |
| 3 Defined | Parcial | Plantillas estándar en `docs/` |
| 4–5 | No | Fuera de alcance académico |
