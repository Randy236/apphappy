# Ítem 8 — SonarCloud + Snyk (cobertura y calidad visibles)

## Qué pide el profesor

Que **SonarCloud** muestre métricas de calidad y **cobertura** (JaCoCo), y que **Snyk** reporte el estado de dependencias (sin vulnerabilidades críticas o con plan de corrección).

---

## Parte A — SonarCloud (Android + JaCoCo)

### A1. Desactivar Automatic Analysis (obligatorio)

Si no lo haces, falla con: *"Automatic Analysis is enabled"*.

1. Abre: https://sonarcloud.io/project/analysis_method?id=Randy236_apphappy  
2. **Automatic Analysis → OFF**  
3. Guarda y espera ~1 minuto.

### A2. Token SonarCloud

1. https://sonarcloud.io → **My Account → Security → Generate Token**  
2. Copia el token (solo se muestra una vez).

### A3. Secreto en GitHub

Repo: https://github.com/Randy236/apphappy  

**Settings → Secrets and variables → Actions → New repository secret**

| Nombre | Valor |
|--------|--------|
| `SONAR_TOKEN` | token de SonarCloud |

### A4. Publicar desde tu PC (rápido para capturas)

```powershell
cd D:\apphappy-full
$env:SONAR_TOKEN = "TU_TOKEN_AQUI"
.\scripts\publicar-sonarcloud.ps1
```

O solo JaCoCo local (sin subir):

```powershell
.\scripts\run-coverage-local.ps1
```

### A5. O desde GitHub Actions

**Actions → SonarCloud → Run workflow**

Debe: tests Android → JaCoCo XML → `gradlew sonar` → verde.

### A6. Captura para el informe

Dashboard: https://sonarcloud.io/project/overview?id=Randy236_apphappy  

Debe verse **Coverage** con un % (no "No data").  
Cobertura global baja (~5 %) es normal con mucha UI Compose; en el informe indica que la lógica está en `ui.util` (ítem 5).

---

## Parte B — Snyk (dependencias)

### B1. Token Snyk

1. https://app.snyk.io → cuenta gratis con GitHub  
2. **Account settings → Auth token** → copiar.

### B2. Secreto en GitHub

| Nombre | Valor |
|--------|--------|
| `SNYK_TOKEN` | token de Snyk |

### B3. Ejecutar en CI

**Actions → Snyk Security → Run workflow**

Escanea:

- Proyecto **Gradle** (Android)  
- Proyecto **npm** (`server/package.json`)

### B4. Captura

Panel Snyk → proyecto **apphappy** → último test → captura sin **High/Critical** abiertos (o tabla de excepciones documentadas).

---

## Parte C — Verificación local (checklist)

```powershell
cd D:\apphappy-full
.\scripts\verificar-calidad-item8.ps1
```

Comprueba archivos de config, tests API y (opcional) JaCoCo.

---

## Texto corto para Word

> Se integró **SonarCloud** con el pipeline de GitHub Actions: pruebas unitarias Android, informe **JaCoCo** y publicación del análisis con cobertura visible en el dashboard. **Snyk** analiza dependencias Gradle y npm en CI. Los tokens `SONAR_TOKEN` y `SNYK_TOKEN` están configurados como secretos del repositorio. La cobertura de la API Node se valida con `npm test` (dominio al 100 %, ítem 5); la cobertura en Sonar refleja el módulo Android vía JaCoCo.

---

## Evidencias

Carpeta `docs/evidencias-item8/`:

| # | Captura |
|---|---------|
| 1 | SonarCloud Overview con **Coverage %** |
| 2 | GitHub Actions — workflow **SonarCloud** verde |
| 3 | GitHub Actions — workflow **Snyk Security** |
| 4 | (Opcional) JaCoCo HTML paquete `ui.util` |

Referencias: `docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md`, `docs/informes-item4/INFORME_SONAR_HAPPY_JUMP.md`

---

## Errores frecuentes

| Error | Solución |
|-------|----------|
| Automatic Analysis enabled | A1 — desactivar en SonarCloud |
| Unauthorized / 401 Sonar | Regenerar `SONAR_TOKEN` en GitHub |
| Coverage "No data" | Ejecutar `jacocoTestReport` antes de `sonar` |
| Snyk skipped | Crear `SNYK_TOKEN` en GitHub Secrets |

Cuando tengas capturas: **「listo el 8」** — checklist de entregables completo.
