# Documento de avance — Happy Jump

**Continuación del primer entregable — Unidad 1**  
**Proyecto:** Sistema de reservas Happy Jump (Android + API Node.js + MySQL)  
**Repositorio:** https://github.com/Randy236/apphappy  
**Versión del documento:** 2.0  
**Fecha:** __________________  
**Equipo:** __________________  

---

## Control del documento

| Campo | Valor |
|-------|--------|
| Documento anterior (Unidad 1) | Entregable 1 — Requerimientos, arquitectura, plan inicial, prototipos conceptuales |
| Documento actual (Unidad 2) | Avance de implementación, calidad, procesos y evidencias |
| Identificador | AV-HAPPYJUMP-U2-2026 |

### Historial de revisiones

| Versión | Fecha | Autor | Cambio |
|---------|--------|--------|--------|
| 1.0 | ___/___/2026 | Equipo | Primer entregable Unidad 1 (requerimientos y diseño) |
| 2.0 | ___/___/2026 | Equipo | Documento de avance — continuidad Unidad 2 |

---

## 1. Resumen ejecutivo

En la **Unidad 1** se definió el alcance del sistema Happy Jump, los requisitos funcionales, la arquitectura cliente-servidor y el plan de trabajo inicial. En la **Unidad 2** (presente avance) se documenta el **estado de implementación**, las **pruebas**, la **integración CI/CD**, la **gestión ágil (Scrum + Jira)** y el **cumplimiento de calidad** (SonarCloud, Snyk, cobertura, Swagger).

**Avance global estimado del producto:** ___ %  
**Avance de documentación y procesos:** ___ %

---

## 2. Continuidad con el entregable 1 (Unidad 1)

### 2.1 Lo entregado en Unidad 1 (referencia)

| Ítem Unidad 1 | Evidencia en el repositorio | Estado al cierre U1 |
|---------------|----------------------------|---------------------|
| Requerimientos del sistema | `docs/REQUERIMIENTOS.csv` | Definido |
| Matriz actividades ↔ requisitos | `docs/MATRIZ_ACTIVIDADES_REQUERIMIENTOS.csv` | Definido |
| Arquitectura cliente-servidor | README, capas `app/` + `server/` | Definido |
| Plan de pruebas (borrador) | `docs/PLAN_DE_PRUEBAS.md` | Borrador |
| Prototipos / visión UI | `docs/prototipos/` (capturas app) | Parcial |

### 2.2 Cómo avanza la Unidad 2 respecto a la Unidad 1

| Promesa U1 | Avance U2 (este documento) |
|------------|----------------------------|
| App móvil para reservas | App Android Compose operativa: login, cancha, salones, admin, reportes |
| API REST + MySQL | API Express en `server/` con JWT, sesión única, CRUD reservas |
| Calidad y pruebas | Plan maestro formal, Postman, tests unitarios, CI GitHub Actions |
| Gestión de proyecto | Scrum + CMMI documentado; backlog Jira importable |
| Despliegue / operación | Scripts `ci/`, Jenkins + Docker documentados |

---

## 3. Alcance funcional implementado (avance técnico)

### 3.1 Módulos entregados

| Módulo | Descripción | Avance | Evidencia |
|--------|-------------|--------|-----------|
| Autenticación | Login nombre + PIN, JWT, logout, cambio PIN | 100 % | `POST /auth/login`, pantalla Login |
| Cancha deportiva | Listar, crear, cobrar saldo, cancelar | 100 % | `reservas-cancha/*`, UI Cancha |
| Salones | CRUD reservas, roles trabajador/admin | 100 % | `reservas-salones/*`, UI Salones |
| Reportes | Ingresos y cancelaciones (admin) | 100 % | `GET /reportes`, `GET /reportes/cancelaciones` |
| Calidad API | Health, Swagger UI, tests, k6 parcial | En progreso | `openapi.json`, `server/test/` |

### 3.2 Stack tecnológico (sin cambio respecto a U1)

| Capa | Tecnología |
|------|------------|
| Cliente | Kotlin, Jetpack Compose, MVVM, Retrofit |
| Servidor | Node.js, Express, JWT, bcrypt |
| Datos | MySQL 8 (`happy_jump`) |
| CI | GitHub Actions, Jenkins + Docker (laboratorio) |
| Calidad | SonarCloud, Snyk, JaCoCo, Postman, k6 |

---

## 4. Avance por área de proceso (TS, VER, IP)

*Alineado con las tres áreas de trabajo en Jira (ver entregable 2).*

| Área | Significado | Actividades completadas | % avance | Pendiente |
|------|-------------|-------------------------|----------|-----------|
| **TS** | Tecnología / solución técnica | Código app + API, Swagger base, CI scripts | ___ % | Swagger 100 %, soft delete global |
| **VER** | Verificación y validación | Plan maestro pruebas, CP-xxx, Postman, tests unitarios API/Android | ___ % | Cobertura 100 %, k6 todos los endpoints |
| **IP** | Implementación y procesos | Scrum+CMMI doc, Jira CSV, GitHub repo, Jenkins guía | ___ % | 2 reviews GitHub, Sonar/Snyk en verde |

---

## 5. Pruebas y calidad (avance)

| Actividad | Herramienta | Estado | Métrica actual | Meta |
|-----------|-------------|--------|----------------|------|
| Pruebas unitarias API | Node `node:test` | Operativo | 4 tests OK | 100 % cobertura |
| Pruebas unitarias Android | JUnit + JaCoCo | Operativo | ~5 % líneas global | Meta curso / lógica 100 % |
| Pruebas integración API | Postman Collection Runner | Operativo | ~15 requests | Evidencia capturas |
| Carga / estrés API | k6 | Parcial | `/hello`, `/sumar`, smoke | Todos los endpoints |
| Análisis estático | SonarCloud | Configurado | Coverage pendiente subida | Visible en dashboard |
| Dependencias | Snyk | Workflow configurado | Requiere `SNYK_TOKEN` | Informe sin críticos |
| Documentación API | Swagger UI | Operativo | `/swagger-ui/` | 100 % rutas documentadas |

**Referencias:** `docs/PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md`, `docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md`

---

## 6. Integración continua y despliegue (avance)

| Componente | Estado | Evidencia |
|------------|--------|-----------|
| GitHub Actions — API Tests | Configurado | `.github/workflows/api-tests.yml` |
| GitHub Actions — SonarCloud | Configurado (coverage en ajuste) | `sonarcloud.yml` |
| GitHub Actions — Snyk | Configurado | `snyk.yml` |
| Jenkins + Docker | Documentado + scripts | `Jenkinsfile`, `docs/GUIA_SALON_JENKINS_DOCKER_PASO_A_PASO.md` |
| Build / deploy scripts | Implementado | `ci/build-production.sh`, `ci/deploy-server.sh` |

---

## 7. Gestión del proyecto (avance)

| Artefacto | Estado | Ubicación |
|-----------|--------|-----------|
| Scrum + CMMI | Documentado | `docs/SCRUM_CMMI_HAPPY_JUMP.md` |
| Backlog Jira (import CSV) | Listo | `docs/jira-import/happy-jump-backlog-scrum-cmmi.csv` |
| Casos de prueba | Listo | `docs/CASOS_DE_PRUEBA.csv` |
| Cronograma / Gantt | Referencia | `docs/CRONOGRAMA_GANTT*.md` (si aplica) |
| Colaboración GitHub (reviews) | Pendiente | Ver entregable 3 |

---

## 8. Riesgos y problemas

| ID | Riesgo / problema | Impacto | Mitigación | Estado |
|----|-----------------|--------|------------|--------|
| R1 | Cobertura SonarCloud no visible (Automatic Analysis) | Medio | Desactivar autoscan; CI con JaCoCo | En curso |
| R2 | Docker/Jenkins inestable en PC local | Medio | Usar GitHub Actions + PC salón | Mitigado |
| R3 | 100 % cobertura en UI Compose | Alto | Priorizar API + dominio; documentar excepción | Planificado |
| R4 | Falta soft delete uniforme en BD | Medio | Migración `deleted_at` todas las tablas | Pendiente |

---

## 9. Plan de trabajo — próximas 2 semanas

| Prioridad | Entregable | Responsable | Fecha meta |
|-----------|------------|-------------|------------|
| 1 | Completar documento avance + firmas | Equipo | ___ |
| 2 | Jira TS / VER / IP + capturas | ___ | ___ |
| 3 | 2 PR con review aprobada en GitHub | ___ | ___ |
| 4 | Swagger 100 % + soft delete | ___ | ___ |
| 5 | Tests + k6 + Sonar/Snyk | ___ | ___ |

---

## 10. Conclusiones

El proyecto Happy Jump **continúa de forma coherente** con el primer entregable de la Unidad 1: los requisitos definidos se reflejan en código operativo (app + API), con trazabilidad en matrices y casos de prueba. La Unidad 2 consolida **procesos de calidad y gestión** (Scrum, CMMI, CI/CD) y deja identificados los pendientes medibles: cobertura al 100 %, documentación Swagger completa, k6 integral, eliminado lógico en entidades y evidencia de colaboración en GitHub.

---

## 11. Anexos

| Anexo | Descripción |
|-------|-------------|
| A | `docs/REQUERIMIENTOS.csv` |
| B | `docs/CASOS_DE_PRUEBA.csv` |
| C | `docs/PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md` |
| D | Capturas: GitHub Actions, Postman, Swagger, Jira |
| E | `docs/CHECKLIST_ENTREGABLES_FINAL.md` |

---

## Instrucciones para entregar (copiar a Word)

1. Abre este archivo en Word o copia el contenido.
2. Completa campos `___` (fechas, nombres, porcentajes reales).
3. Inserta capturas en secciones 5, 6 y 7.
4. En la carátula indica: **“Documento de avance — continuidad Entregable 1, Unidad 1”**.
5. Exporta PDF: `DOCUMENTO_AVANCE_UNIDAD2.pdf`.

---

*Plantilla generada para el equipo Happy Jump — ítem 1 de entregables finales.*
