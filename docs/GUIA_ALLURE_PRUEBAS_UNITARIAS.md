# Allure — reportes de pruebas unitarias

## Qué es Allure

Herramienta que genera un **reporte HTML** bonito con suites, casos, duración y estado (passed/failed).

Happy Jump integra Allure en:

| Capa | Tests | Reporte |
|------|-------|---------|
| **Android** | JUnit 4 (`app/src/test`) | Plugin Gradle Allure |
| **API** | Node (`server/test`) | JUnit XML → mismo reporte |

---

## Ejecutar todo (recomendado)

```powershell
cd D:\apphappy-full
.\scripts\run-allure-report.ps1
```

Abre el HTML en el navegador al terminar.

---

## Solo Android

```powershell
cd D:\apphappy-full
.\gradlew.bat :app:testDebugUnitTest :app:allureReport
```

Reporte generado en:

```text
app\build\reports\allure-report\allureReport\index.html
```

**No abras ese HTML con doble clic** (Chrome muestra solo "Loading..." por seguridad `file://`).

### Ver el reporte (elige uno)

**Opción A — Allure serve (recomendado):**

```powershell
cd D:\apphappy-full
.\gradlew.bat :app:allureServe
```

Abre la URL que imprime (ej. http://127.0.0.1:xxxxx). **No cierres** PowerShell hasta terminar.

**Opción B — Python:**

```powershell
cd D:\apphappy-full\app\build\reports\allure-report\allureReport
python -m http.server 8765
```

Navegador: **http://localhost:8765**

---

## Solo API (para Allure combinado)

```powershell
cd D:\apphappy-full\server
npm run test:junit
```

Genera XML en `app/build/test-results/apiUnit/`. Luego corre `:app:allureReport` para incluirlo.

---

## Evidencia para el profesor

Capturas del reporte Allure:

1. Overview (total passed)
2. Suites → **Android util** y **API**
3. Detalle de un test

Carpeta sugerida: `docs/evidencias-allure/`

---

## Texto corto para Word

> Las pruebas unitarias de la API (Node) y de Android (JUnit) se ejecutan con reporte **Allure**, que consolida resultados en HTML para revisión y entrega académica.
