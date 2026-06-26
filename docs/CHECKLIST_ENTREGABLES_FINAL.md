# Checklist entregables finales — Happy Jump

Trabajar **uno por uno**. Marca cada ítem al terminar.

| # | Entregable | Estado | Guía / archivo |
|---|------------|--------|----------------|
| 1 | Documento de avance (continúa Unidad 1) | 🟡 En progreso | `DOCUMENTO_AVANCE_UNIDAD2.md` |
| 2 | CMMI + Scrum + Jira (áreas **TS**, **VER**, **IP**) | 🟡 En progreso | `GUIA_ITEM2_JIRA_CMMI_TS_VER_IP.md` |
| 3 | GitHub colaborativo (mín. **2 reviews** aprobación) | 🟡 En progreso | `GUIA_ITEM3_GITHUB_REVIEWS_COLABORATIVO.md` |
| 4 | Swagger documentación (**100%** endpoints) | ✅ Listo | `GUIA_ITEM4_SWAGGER_100.md` + `npm run swagger:verify` |
| 5 | Cobertura pruebas unitarias **100%** | ✅ Listo | `GUIA_ITEM5_COBERTURA_UNITARIA_100.md` + `npm run coverage:verify` |
| 6 | k6 de **todos** los controllers/rutas API | ✅ Listo | `GUIA_ITEM6_K6_TODOS_ENDPOINTS.md` + `k6/api-all-endpoints.js` |
| 7 | Eliminado lógico en **todas** las entidades | ✅ Listo | `GUIA_ITEM7_ELIMINADO_LOGICO.md` + `npm run migrate:009` |
| 8 | SonarCloud + Snyk reflejan cobertura | 🟡 En progreso | `GUIA_ITEM8_SONARCLOUD_SNYK.md` + tokens GitHub |

---

## Orden recomendado

```
1 → 2 → 3 → 4 → 7 → 5 → 6 → 8
```

(El 7 ayuda al 5; Swagger y tests van juntos en API.)

---

## Estado técnico actual (referencia rápida)

| Área | Hoy | Meta |
|------|-----|------|
| API routes | ~15 endpoints en `index.js` | Swagger 100% |
| Tests API | 6 módulos `domain/` + tests Node | `npm test` + `coverage:verify` |
| Tests Android | ~25 tests (`ui.util`) | 100% cobertura global difícil en UI |
| k6 | `/hello`, `/sumar`, carga básica | Todos los endpoints |
| Soft delete | `cancelada` / `estado cancelado` parcial | `deleted_at` en todas las tablas |
| SonarCloud | CI + `publicar-sonarcloud.ps1` | Coverage % en dashboard |
| Snyk | Gradle + server npm en CI | `SNYK_TOKEN` en GitHub |
| Jira | CSV import listo | Boards TS / VER / IP |
| GitHub reviews | Por configurar | 2+ PRs aprobados |

---

*Actualiza este archivo al cerrar cada entregable.*
