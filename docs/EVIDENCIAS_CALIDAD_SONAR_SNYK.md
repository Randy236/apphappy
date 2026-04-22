# Evidencias de calidad — análisis estático y seguridad (SonarCloud + Snyk)

**Proyecto:** Happy Jump (`apphappy`)  
**Propósito:** documento listo para entrega académica: enlaces, qué mide cada herramienta, cómo reproducir resultados y espacio para pegar métricas o capturas tras el último análisis.

---

## 1. Resumen ejecutivo

| Ámbito | Herramienta | Qué aporta |
|--------|-------------|------------|
| Calidad y deuda técnica del código Kotlin/XML | **SonarCloud** | Bugs, vulnerabilidades, *code smells*, duplicación, cobertura (vía JaCoCo en CI) |
| Riesgos en dependencias (Gradle, transitivas) | **Snyk** | Vulnerabilidades conocidas (CVE) en librerías; umbral configurado en CI |

Ambas se integran con **GitHub Actions** en este repositorio (ver sección 4).

---

## 2. SonarCloud

### 2.1 Configuración en el repo

- Archivo: `sonar-project.properties` (`projectKey`, `organization`, rutas de fuentes, tests y reporte XML de JaCoCo).
- Plugin Gradle: `org.sonarqube` en `build.gradle.kts`.
- Workflow: `.github/workflows/sonarcloud.yml` (tests unitarios + `jacocoTestReport` + `sonar`).

### 2.2 Secretos en GitHub (obligatorio para CI)

1. En [SonarCloud](https://sonarcloud.io): **Account → Security → Generate Tokens** (análisis).
2. En el repo GitHub: **Settings → Secrets and variables → Actions**.
3. Crear **`SONAR_TOKEN`** con el token de SonarCloud.

Sin este secreto, el job de Sonar no podrá publicar el análisis en el push/PR.

### 2.3 Cómo ver el tablero

Abre el proyecto en SonarCloud (misma organización y `projectKey` que en `sonar-project.properties`) y revisa:

- **Reliability / Security / Maintainability**
- **Coverage** (después de que el workflow suba el XML de JaCoCo)
- **Duplications** y **Security Hotspots** (revisión manual recomendada en cursos de auditoría)

### 2.4 Tabla para el profesor (completar tras cada entrega)

*Copia aquí los valores del último análisis o adjunta capturas en PDF en la misma carpeta `docs/`.*

| Métrica | Valor (fecha: ______) | Nota breve |
|---------|----------------------|------------|
| Quality Gate | Pasa / No pasa | |
| Bugs | | |
| Vulnerabilidades | | |
| Security Hotspots | | |
| Code Smells | | |
| Cobertura (líneas) | % | Generada con JaCoCo en CI |
| Duplicación | % | |

### 2.5 Correcciones aplicadas (bitácora)

| Fecha | Hallazgo (Sonar / revisión) | Acción |
|-------|------------------------------|--------|
| | | |

---

## 3. Snyk

### 3.1 Configuración en el repo

- Workflow: `.github/workflows/snyk.yml`.
- Comando en CI: `snyk test --all-projects --severity-threshold=medium`.

### 3.2 Secretos en GitHub

1. En [Snyk Account](https://app.snyk.io/account): **Auth token** (o cuenta de servicio).
2. En GitHub: secreto **`SNYK_TOKEN`**.

### 3.3 Interpretación

- **High/Critical** en dependencias: actualizar librería, aplicar parche o sustituir dependencia; documentar si hay excepción aceptada en el curso.
- El informe detallado completo sigue en el panel de Snyk (`snyk.io`) enlazado al mismo repositorio.

### 3.4 Tabla para el profesor (completar)

| Fecha del escaneo | Proyectos Gradle analizados | Vulnerabilidades (≥ medium) | Estado |
|-------------------|-----------------------------|-----------------------------|--------|
| | | | |

### 3.5 Correcciones / supresiones documentadas

| Fecha | CVE o paquete | Decisión |
|-------|---------------|----------|
| | | |

---

## 4. Integración continua (GitHub Actions)

| Workflow | Disparadores | Contenido relevante |
|----------|--------------|---------------------|
| `sonarcloud.yml` | `push` / `pull_request` en `main` y `develop` | `:app:testDebugUnitTest`, `:app:jacocoTestReport`, `sonar` |
| `snyk.yml` | `push` / `pull_request` en `main` y `develop`; manual | `snyk test --all-projects` |

**Evidencia para la entrega:** en GitHub → pestaña **Actions**, abrir la última ejecución exitosa y capturar pantalla o exportar log resumido.

---

## 5. Pruebas unitarias (auditoría de lógica en JVM)

### 5.1 Cómo ejecutar en local

```bash
./gradlew :app:testDebugUnitTest :app:jacocoTestReport
```

- Informe HTML JaCoCo: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML para Sonar: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

### 5.2 Clases de prueba (trazabilidad)

| Archivo | Enfoque |
|---------|---------|
| `ExampleUnitTest.kt` | Franjas de cancha, hora SQL, reparto de montos |
| `TimeUtilTest.kt` | Fechas API ISO, duración de bloques, validación de rangos |
| `CanchaGrupoTurnoTest.kt` | Agrupación de turnos contiguos, solape con slots, formato de duración |
| `CanchaDisponibilidadCalendarioTest.kt` | Semáforo de calendario (libre / parcial / pasado) |

Estas pruebas cubren **reglas de negocio puras** (sin emulador), alineadas con el plan maestro de pruebas y con la auditoría estática: menos riesgo de regresión cuando Sonar o refactors tocan el mismo código.

### 5.3 Resultados de la última corrida local o CI

| Fecha | Tests ejecutados | Fallidos | Notas |
|-------|------------------|----------|-------|
| | | | |

---

## 6. Enlaces rápidos para el informe del curso

- Repositorio: sustituir por la URL pública de tu GitHub.
- SonarCloud: proyecto con el mismo `projectKey` que `sonar-project.properties`.
- Snyk: proyecto vinculado al repositorio (si aplica).

---

*Documento generado para complementar `PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md` y la checklist de entrega.*
