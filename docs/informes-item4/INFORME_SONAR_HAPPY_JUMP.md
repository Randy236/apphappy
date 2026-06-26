# INFORME DE ANÁLISIS ESTÁTICO — SONARQUBE / SONARCLOUD

**Proyecto:** Happy Jump  
**Repositorio:** https://github.com/Randy236/apphappy  
**Módulo analizado:** aplicación Android (`app/`)  
**Herramienta:** SonarCloud (SonarQube en la nube)  
**Organización / projectKey:** randy236 / Randy236_apphappy  
**Fecha del informe:** __________________  
**Integrantes:** __________________  

---

## 1. Objetivo

Documentar los resultados del análisis estático de calidad de código del proyecto Happy Jump mediante SonarCloud, incluyendo métricas de confiabilidad, seguridad, mantenibilidad y cobertura de pruebas unitarias (JaCoCo).

---

## 2. Alcance

| Incluye | Excluye |
|---------|---------|
| Código Kotlin en `app/src/main/java` | Backend Node (`server/`) en este análisis Android |
| Pruebas unitarias JVM en `app/src/test/java` | Pruebas E2E / instrumentadas en emulador |
| Pipeline CI GitHub Actions (`sonarcloud.yml`) | Despliegue en producción |

---

## 3. Metodología

1. Configuración en el repositorio: `sonar-project.properties`, plugin Gradle `org.sonarqube`, workflow `.github/workflows/sonarcloud.yml`.
2. Ejecución de pruebas unitarias Android: `./gradlew :app:testDebugUnitTest`.
3. Generación de cobertura JaCoCo: `./gradlew :app:jacocoTestReport`.
4. Publicación del análisis a SonarCloud (CI o script local `scripts/publicar-sonarcloud.ps1`).
5. Revisión del dashboard en https://sonarcloud.io/project/overview?id=Randy236_apphappy.

**Clases de prueba unitaria (lógica de negocio):**

- `TimeUtilTest.kt` — fechas, duración de bloques, rangos horarios.
- `CanchaGrupoTurnoTest.kt` — agrupación de turnos y solapes.
- `CanchaDisponibilidadCalendarioTest.kt` — semáforo del calendario.
- `ExampleUnitTest.kt` — reglas auxiliares (franjas, montos).

---

## 4. Resultados (completar con datos reales)

### 4.1 Resumen de métricas SonarCloud

| Métrica | Valor | Interpretación breve |
|---------|-------|----------------------|
| Quality Gate | | Aprobado / no aprobado |
| Bugs | | |
| Vulnerabilidades | | |
| Security Hotspots | | |
| Code Smells | | |
| Cobertura (líneas) | % | JaCoCo vía CI o local |
| Duplicación | % | |

*Fuente: SonarCloud Overview, fecha __________.*

### 4.2 Cobertura JaCoCo (pruebas unitarias)

| Métrica | Cubiertas | No cubiertas | % |
|---------|-----------|--------------|---|
| Líneas (LINE) | 168 | 3047 | ~5,23 % |
| Instrucciones (INSTRUCTION) | 1292 | 31918 | ~3,89 % |

*Comando: `.\scripts\run-coverage-local.ps1` o workflow SonarCloud.*

**Nota:** El porcentaje global es bajo porque gran parte del código son pantallas Jetpack Compose sin tests que las ejecuten; la lógica en el paquete `com.example.happyj.ui.util` concentra la mayor cobertura relativa.

### 4.3 Integración continua

| Workflow GitHub | Contenido | Estado (captura) |
|-----------------|-----------|------------------|
| `sonarcloud.yml` | Tests + JaCoCo + análisis Sonar | |
| `api-tests.yml` | Tests unitarios API Node | |

---

## 5. Hallazgos relevantes (ejemplos — reemplazar)

| # | Tipo | Severidad | Descripción | Acción propuesta |
|---|------|-----------|-------------|------------------|
| 1 | Code Smell | | | |
| 2 | Bug | | | |
| 3 | Cobertura | | UI sin tests | Agregar tests o documentar excepción |

---

## 6. Correcciones aplicadas (bitácora)

| Fecha | Hallazgo | Acción realizada |
|-------|----------|------------------|
| | | |

---

## 7. Conclusiones

1. El proyecto cuenta con integración SonarCloud y pipeline de calidad en GitHub Actions.
2. Las pruebas unitarias JVM validan reglas de negocio críticas (horarios, disponibilidad de cancha).
3. La cobertura global refleja la proporción de UI Compose frente a lógica testeada; se recomienda ampliar tests en capas ViewModel o utilidades puras.
4. El Quality Gate y las métricas de seguridad deben revisarse en cada sprint antes de entrega.

---

## 8. Anexos

- Captura A: SonarCloud Overview  
- Captura B: GitHub Actions (workflow SonarCloud o API Tests)  
- Captura C: Informe JaCoCo HTML o consola `printJacocoTotals`  
- Archivo Excel: `METRICAS_SONAR_HAPPY_JUMP.xlsx`  
