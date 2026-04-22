# Checklist entrega — Profesor (ítems 2, 3 y 4)

## 2. Prototipos — revisar / completar

| Estado | Nota |
|--------|------|
| **En el repo no hay carpeta fija de prototipos** (mockups Figma, PDF de bocetos, etc.). | Lo que suele aceptarse como prototipo: **capturas de pantalla** de la app en emulador o celular (Login, Cancha, Salones, Admin, Perfil). |

**Qué hacer antes de subir:**

- Crear carpeta `docs/prototipos/` y guardar ahí **PNG o PDF** con pantallas principales (5–10 imágenes).
- Opcional: enlace a **Figma** o **Canva** en un `README.txt` dentro de esa carpeta.
- Si el curso pide «prototipo previo al código», indica en la carátula que son **prototipos de alta fidelidad** basados en la app implementada.

---

## 3. Plan de pruebas — listo

| Archivo | Uso |
|---------|-----|
| `docs/PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md` | **Plan maestro formal** (control del documento, estrategia, ambientes, defectos, métricas). **Entregar este como «Plan de Pruebas».** |
| `docs/PLAN_DE_PRUEBAS.md` | Resumen operativo corto; puedes anexarlo como versión breve. |

---

## 4. Casos de prueba — listo

| Archivo | Uso |
|---------|-----|
| `docs/CASOS_DE_PRUEBA.csv` | Casos **CP-001 …** con módulo, pasos, resultado esperado, prioridad. Abre con **Excel** o **Google Sheets**; si piden `.xlsx`, exporta desde ahí. |

**Trazabilidad opcional:** `docs/REQUERIMIENTOS.csv` y `docs/MATRIZ_ACTIVIDADES_REQUERIMIENTOS.csv`.

---

## 5. Calidad — SonarCloud, Snyk, unit tests

| Archivo / acción | Uso |
|------------------|-----|
| `docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md` | Plantilla profesional: enlaces, secretos GitHub, tablas para pegar métricas y bitácora de correcciones. |
| GitHub → **Actions** | Capturas de jobs *SonarCloud* y *Snyk Security* como evidencia. |
| SonarCloud / Snyk (web) | Capturas del *Quality Gate* y del informe de vulnerabilidades (última clase / entrega). |
| `./gradlew :app:testDebugUnitTest :app:jacocoTestReport` | Reproducir tests y cobertura local. |

---

## Resumen rápido

| Ítem | Estado |
|------|--------|
| 2. Prototipos | **Pendiente:** agregar `docs/prototipos/` con capturas o PDF. |
| 3. Plan de pruebas | **Listo:** `PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md` |
| 4. Casos de prueba | **Listo:** `CASOS_DE_PRUEBA.csv` |
| 5. Sonar / Snyk / auditoría | **Listo (plantilla + CI):** `EVIDENCIAS_CALIDAD_SONAR_SNYK.md` — completar tablas con números reales antes de entregar. |
