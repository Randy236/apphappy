# Plan maestro de pruebas de software — Happy Jump

**Proyecto:** Happy Jump — gestión de reservas (cancha deportiva y salones)  
**Stack:** aplicación Android (Kotlin, Jetpack Compose) + API Node.js (Express) + MySQL  
**Estándar de referencia:** IEEE 829-2008 / ISO/IEC/IEEE 29119-3:2021 (adaptado a proyecto académico–operativo)

---

## Control del documento

| Campo | Valor |
|--------|--------|
| **Identificador** | TP-HAPPYJUMP-2026-001 |
| **Proyecto** | Happy Jump — sistema de reservas y operación en centro de entretenimiento |
| **Documentos de referencia** | `REQUERIMIENTOS.csv`, `CASOS_DE_PRUEBA.csv`, `MATRIZ_ACTIVIDADES_REQUERIMIENTOS.csv`, `PLAN_DE_PRUEBAS.md` (resumen operativo), `EVIDENCIAS_CALIDAD_SONAR_SNYK.md` (SonarCloud, Snyk, CI, unit tests) |
| **Autor principal** | Equipo de desarrollo / responsable de calidad del proyecto |
| **Revisor sugerido** | Profesor del curso / arquitecto de software |
| **Aprobador sugerido** | Product owner / representante de la empresa |

---

## Historial de revisiones

| Versión | Fecha | Autor | Descripción |
|---------|--------|--------|-------------|
| 0.1 | 14/04/2026 | Proyecto Happy Jump | Borrador: alcance, estrategia, niveles de prueba |
| 1.0 | 14/04/2026 | Proyecto Happy Jump | Versión para entrega académica: ambientes, defectos, métricas, trazabilidad |
| 1.1 | 22/04/2026 | Proyecto Happy Jump | CI SonarCloud + Snyk, ampliación de pruebas unitarias, documento `EVIDENCIAS_CALIDAD_SONAR_SNYK.md` |

---

## Distribución

| Nombre / rol | Propósito |
|--------------|-----------|
| Profesor / evaluador | Revisión del plan y criterios de aceptación |
| Desarrollador / tester del proyecto | Ejecución de pruebas y registro de resultados |
| Operación Happy Jump (referencia) | Validación de negocio en UAT informal si aplica |

---

## Tabla de contenidos

1. [Introducción](#1-introducción)  
   1.1. Propósito del plan de pruebas  
   1.2. Alcance de las pruebas  
   1.3. Referencias  
2. [Estrategia de pruebas](#2-estrategia-de-pruebas)  
3. [Criterios de entrada y salida](#3-criterios-de-entrada-y-salida)  
4. [Gestión de defectos](#4-gestión-de-defectos)  
5. [Ambientes de prueba](#5-ambientes-de-prueba)  
6. [Recursos, herramientas y organización](#6-recursos-herramientas-y-organización)  
7. [Cronograma e hitos](#7-cronograma-e-hitos)  
8. [Métricas de calidad y reportes](#8-métricas-de-calidad-y-reportes)  
9. [Trazabilidad](#9-trazabilidad)

---

## 1. Introducción

### 1.1. Propósito del plan de pruebas

Este plan define **qué** se prueba, **cómo** y **con qué criterios** en el sistema Happy Jump, de modo que:

- Se verifiquen los requisitos funcionales documentados.
- Se reduzca el riesgo de fallos en producción (reservas incorrectas, pérdida de datos, accesos indebidos).
- Se deje constancia repetible para evaluación académica y mejora continua.

### 1.2. Alcance de las pruebas

#### 1.2.1. Funcionalidades incluidas (dentro del alcance)

- Autenticación por nombre y PIN; roles trabajador / administrador; cierre de sesión.
- Sesión única por usuario (según reglas de la API).
- Módulo **Cancha**: consulta por día/semana/calendario, reserva por grilla, reserva por rango («Otra hora»), estados de pago, cancelación con motivo, agrupación visual de turnos largos.
- Módulo **Salones**: consulta y registro de reservas de eventos.
- **Reportes** y funciones de **administración** según la build entregada.
- **Perfil**: cambio de PIN cuando aplique.
- **API REST**: validaciones de fechas, horarios, solapes y persistencia en MySQL.

#### 1.2.2. Funcionalidades excluidas (fuera del alcance)

- Pruebas de carga o estrés a gran escala.
- Certificación oficial en tiendas (Google Play) o compatibilidad exhaustiva con todos los modelos de dispositivo.
- Pentesting profesional o auditoría de seguridad formal.
- App nativa iOS (no forma parte del entregable actual).

### 1.3. Referencias

| Documento | Ubicación en el repositorio |
|-----------|-----------------------------|
| Requisitos | `docs/REQUERIMIENTOS.csv` |
| Casos de prueba | `docs/CASOS_DE_PRUEBA.csv` |
| Matriz actividades–requisitos | `docs/MATRIZ_ACTIVIDADES_REQUERIMIENTOS.csv` |
| Notas matriz | `docs/MATRIZ_ACTIVIDADES_NOTAS.md` |
| Plan operativo resumido | `docs/PLAN_DE_PRUEBAS.md` |
| Código fuente app | `app/` |
| Código fuente API | `server/` |
| Pruebas unitarias (JVM) | `app/src/test/java/com/example/happyj/ExampleUnitTest.kt`, `.../ui/util/TimeUtilTest.kt`, `CanchaGrupoTurnoTest.kt`, `CanchaDisponibilidadCalendarioTest.kt` |

---

## 2. Estrategia de pruebas

### 2.1. Niveles de prueba y modelo en V (resumen)

| Nivel | Enfoque en Happy Jump |
|-------|------------------------|
| **Unitarias** | Lógica pura en Kotlin (horarios, franjas de cancha, reparto de montos) ejecutada con JUnit en JVM. |
| **Integración** | App ↔ API (Retrofit); API ↔ MySQL; verificación de JSON, códigos HTTP y reglas de negocio en servidor. |
| **Sistema** | Flujos completos en dispositivo o emulador: login → cancha/salones → reportes. |
| **Aceptación (UAT)** | Validación informal con usuario de negocio o criterios del profesor sobre casos críticos. |

**Nota:** En un proyecto de tamaño académico–Pyme, la proporción de esfuerzo suele inclinar el sistema y la integración manual; las unitarias cubren módulos críticos de cálculo. Los porcentajes del documento tipo comercio (p. ej. 55 % unitarias) son orientativos; aquí se prioriza **trazabilidad REQ ↔ CP** y **smoke + regresión** antes de un porcentaje rígido.

### 2.2. Tipos de prueba por nivel

| Tipo | Nivel principal | Descripción |
|------|-----------------|-------------|
| Funcional | Sistema | Casos CP-001 … CP-014 y extensiones según `CASOS_DE_PRUEBA.csv`. |
| Integración | Integración | Health API, login, CRUD reservas, coherencia BD. |
| Regresión | Sistema | Tras cambios en Cancha, Auth o Salones (CP-014 smoke). |
| Usabilidad básica | Sistema | Mensajes de error, navegación, formularios. |

### 2.3. Pruebas estáticas

- **Revisión de código** y análisis con **SonarQube Cloud** sobre el repositorio `apphappy` (Kotlin/XML), como complemento a las pruebas dinámicas: seguridad, confiabilidad, mantenibilidad, duplicación.
- **Dependencias:** escaneo con **Snyk** en GitHub Actions (`snyk test --all-projects`, umbral *medium*), complementario a SonarCloud.
- **Documentación:** coherencia entre requisitos, matriz y casos de prueba; bitácora de hallazgos y correcciones en `EVIDENCIAS_CALIDAD_SONAR_SNYK.md`.

### 2.4. Pruebas de regresión

- Conjunto mínimo: login, listado/carga cancha, un alta de reserva (si hay datos de prueba), salones, reporte si está habilitado.
- Ejecutar tras cada entrega relevante o merge a la rama acordada (`develop` / `main`).

---

## 3. Criterios de entrada y salida

### 3.1. Criterios de entrada (inicio de una ciclo de pruebas)

- Build Android instalable y API en ejecución con esquema MySQL migrado.
- URL de API correcta en el cliente (emulador, dispositivo o red local).
- Usuarios de prueba disponibles (trabajador / administrador).
- Lista de casos de prueba seleccionados para la corrida (p. ej. todos los P0).

### 3.2. Criterios de salida (cierre de una corrida o hito)

- Casos **P0** ejecutados; **P1** ejecutados salvo defectos aceptados por el profesor.
- Defectos **críticos** resueltos o documentados con plan de mitigación.
- Registro de ejecución: fecha, responsable, versión de app y commit/hash o versión de API si aplica.

---

## 4. Gestión de defectos

### 4.1. Clasificación de severidad (sugerida)

| Severidad | Significado |
|-----------|-------------|
| **Crítica** | Bloqueo: no se puede operar (login caído, pérdida de datos, seguridad grave). |
| **Alta** | Función principal incorrecta (reserva duplicada, cobro mal registrado). |
| **Media** | Error con workaround (mensaje confuso, UI incoherente). |
| **Baja** | Cosmético o mejora menor. |

### 4.2. Clasificación de prioridad

Alineada con el negocio: prioridad **1** = corregir antes de demo o entrega; **2** = siguiente iteración; **3** = backlog.

### 4.3. Flujo de vida de un defecto (simplificado)

Nuevo → Asignado → En corrección → Verificado → Cerrado (o Reabierto si falla la verificación).

### 4.4. Campos mínimos del reporte de defecto

- ID o título breve  
- Pasos para reproducir  
- Resultado esperado / obtenido  
- Severidad y prioridad  
- Entorno (dispositivo, versión app, URL API)  
- Evidencia (captura o log) si es posible  

---

## 5. Ambientes de prueba

### 5.1. Arquitectura de ambientes

| Ambiente | Componentes | Uso |
|----------|-------------|-----|
| **Desarrollo local** | Android Studio + emulador o físico; Node en `localhost`; MySQL local o remoto | Desarrollo y pruebas unitarias |
| **Integración local / red LAN** | APK + API en PC o servidor en Wi‑Fi; mismo MySQL de prueba | CP de integración y sistema |
| **Calidad estática** | SonarQube Cloud enlazado al repositorio GitHub | Análisis sin ejecutar la app completa |

### 5.2. Gestión de datos de prueba

- Usuarios y reservas de prueba documentados o recreados con scripts/migraciones en `server/migrations/` y datos mínimos acordados.
- No usar datos reales de clientes en entornos compartidos sin anonimizar.

### 5.3. Pipeline CI/CD y quality gates

- **Estado actual:** en GitHub Actions se ejecutan **pruebas unitarias + JaCoCo**, análisis **SonarCloud** y escaneo **Snyk** en ramas `main` y `develop` (y PRs), con secretos `SONAR_TOKEN` y `SNYK_TOKEN`.
- **Objetivo:** mantener quality gate en SonarCloud y revisar hotspots de seguridad; registrar métricas y capturas en `EVIDENCIAS_CALIDAD_SONAR_SNYK.md` por entrega.

---

## 6. Recursos, herramientas y organización

### 6.1. Equipo de pruebas (proyecto)

| Rol | Responsabilidad |
|-----|-----------------|
| Desarrollador / tester | Ejecutar casos, unit tests, reportar defectos |
| Profesor | Criterios de aceptación y evaluación |

### 6.2. Herramientas

| Herramienta | Uso |
|-------------|-----|
| Android Studio | IDE, emulador, ejecución de tests |
| Gradle (`gradlew test`) | Pruebas unitarias JVM |
| Postman / cURL / navegador | API, endpoint `/health` |
| SonarQube Cloud | Análisis estático |
| Snyk | Vulnerabilidades en dependencias Gradle |
| GitHub Actions | CI: tests, JaCoCo, Sonar, Snyk |
| Git / GitHub | Control de versiones y trazabilidad |

---

## 7. Cronograma e hitos

| Fase | Actividades |
|------|-------------|
| **Inicio** | Configurar entorno, smoke login y API |
| **Núcleo** | Ejecutar CP cancha y salones; registrar defectos |
| **Regresión** | Tras correcciones; CP-014 o equivalente |
| **Cierre** | Entrega de evidencias (matriz, casos, plan maestro, `EVIDENCIAS_CALIDAD_SONAR_SNYK.md`, capturas Sonar/Snyk/Actions) |

### Hitos de calidad sugeridos

- H1: Todos los CP P0 pasan en entorno acordado.  
- H2: Documentación REQ–CP–matriz consistente.  
- H3: SonarCloud con revisión de hotspots planificada (mejora continua).

---

## 8. Métricas de calidad y reportes

### 8.1. Métricas de proceso

- Número de casos ejecutados / pasados / fallidos por corrida.  
- Defectos abiertos por severidad.  
- Cobertura de requisitos con al menos un caso (trazabilidad matriz).

### 8.2. Métricas de producto (complementarias)

- Indicadores SonarCloud (bugs, vulnerabilidades, code smells, duplicación, cobertura vía JaCoCo) como seguimiento de mantenibilidad y seguridad estática.
- Informe Snyk (dependencias) y resultado de jobs en GitHub Actions como evidencia de auditoría reproducible.

---

## 9. Trazabilidad

- **Requisitos:** `REQUERIMIENTOS.csv` (REQ-001 …).  
- **Casos de prueba:** `CASOS_DE_PRUEBA.csv` (CP-001 …).  
- **Matriz proceso–RF–CUS:** `MATRIZ_ACTIVIDADES_REQUERIMIENTOS.csv`.  
- **Pruebas unitarias:** horarios, franjas, fechas API, agrupación de turnos y semáforo de calendario en `ExampleUnitTest.kt` y tests bajo `app/src/test/.../ui/util/`.

---

*Fin del documento TP-HAPPYJUMP-2026-001*
