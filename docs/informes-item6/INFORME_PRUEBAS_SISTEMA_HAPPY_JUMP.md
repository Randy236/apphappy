# INFORME DE PRUEBAS DE SISTEMA Y END-TO-END (E2E)

**Proyecto:** Happy Jump  
**Repositorio:** https://github.com/Randy236/apphappy  
**Fecha de ejecución:** __________________  
**Responsable:** __________________  
**Versión app / commit:** __________________  
**Entorno API:** http://_____________:3000  

---

## 1. Introducción

Este informe documenta las **pruebas de sistema** y **flujos end-to-end (E2E)** del sistema Happy Jump, compuesto por:

- **Cliente Android** (`app/`) — reservas de cancha y salones, roles trabajador/administrador  
- **API REST** (`server/`) — Node.js, Express, MySQL  
- **Base de datos** MySQL  

Las pruebas se diseñaron según el catálogo **`docs/CASOS_DE_PRUEBA.csv`** (casos CP-001 a CP-022) y el **Plan maestro de pruebas** (`docs/PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md`).

---

## 2. Objetivos

- Verificar que los flujos críticos de negocio funcionen de extremo a extremo (usuario → app → API → base de datos).
- Diferenciar evidencia de pruebas **manuales** (UI en dispositivo) y **automatizadas** (API, CI, scripts).
- Registrar resultados trazables a cada caso de prueba CP-XXX.

---

## 3. Alcance

| Incluido | Excluido |
|----------|----------|
| Login, Cancha, Salones, Reportes, Perfil | Pruebas de carga masiva (salvo smoke k6) |
| Integración app ↔ API ↔ MySQL | Dispositivos Android compatibles con minSdk 24 |
| Casos P0 y P1 del CSV | Pentesting profesional |

---

## 4. Entorno de prueba

| Componente | Detalle |
|------------|---------|
| Dispositivo / emulador | __________________ |
| SO Android | __________________ |
| PC servidor API | Windows, Node 22, MySQL |
| Red | Misma Wi‑Fi / `adb reverse` |
| Herramientas | Android Studio, Postman, npm, k6 (opcional) |

**Precondición global:** API activa y `GET /health` responde `ok: true` (caso **CP-006**).

---

## 5. Casos de prueba de referencia

Fuente: **`docs/CASOS_DE_PRUEBA.csv`**

| Rango | Módulos |
|-------|---------|
| CP-001 – CP-005 | Login, autenticación |
| CP-006 | Health API |
| CP-007 – CP-009, CP-015 – CP-022 | Cancha |
| CP-010 – CP-011 | Salones |
| CP-012 – CP-013 | Reportes, perfil |
| CP-014 | Smoke regresión |

Prioridad **P0** = obligatorios para esta entrega.

---

## 6. Pruebas manuales (E2E / sistema)

### 6.1 Metodología

1. Instalación de build Android (debug/release).  
2. Configuración de URL de API hacia el servidor de pruebas.  
3. Ejecución paso a paso según columna **Pasos** del CSV.  
4. Comparación con **Resultado esperado**.  
5. Registro en matriz de ejecución y captura de pantalla por caso crítico.

### 6.2 Resultados resumidos (completar)

| ID | Caso | Prioridad | Resultado | Observaciones |
|----|------|-----------|-----------|---------------|
| CP-001 | Login trabajador válido | P0 | Pasó / Falló | |
| CP-002 | Login admin válido | P0 | Pasó / Falló | |
| CP-003 | Credenciales incorrectas | P0 | Pasó / Falló | |
| CP-006 | Health API | P0 | Pasó / Falló | |
| CP-007 | Listar reservas día | P0 | Pasó / Falló | |
| CP-008 | Crear reserva válida | P0 | Pasó / Falló | |
| CP-010 | Listar salones | P0 | Pasó / Falló | |
| CP-011 | Crear reserva salón | P0 | Pasó / Falló | |
| CP-014 | Smoke regresión | P1 | Pasó / Falló | |
| CP-015 | Calendario colores | P0 | Pasó / Falló | |
| CP-018 | Turno largo una fila | P0 | Pasó / Falló | |
| CP-020 | Otra hora reserva | P0 | Pasó / Falló | |
| CP-021 | Duración media hora | P0 | Pasó / Falló | |

*Ampliar con el resto de casos según tiempo disponible.*

### 6.3 Evidencias manuales (insertar capturas)

- **Figura 1:** CP-001 — Pantalla login exitoso  
- **Figura 2:** CP-007 — Listado cancha por día  
- **Figura 3:** CP-008 — Reserva creada  
- **Figura 4:** CP-015 — Calendario con semáforo de colores  
- **Figura 5:** CP-006 — Respuesta `/health` en navegador o Postman  

---

## 7. Pruebas automatizadas

### 7.1 API — pruebas unitarias (Node)

**Herramienta:** `node --test` (script `npm run test:ci`)

```powershell
cd server
npm ci
npm run test:ci
```

**Qué valida:** reglas de dominio (ej. estado de cancha) sin UI.

**Resultado:** _____ tests pasados / _____ fallidos  
**Evidencia:** captura de terminal o job **API Tests** en GitHub Actions.

**Casos relacionados:** lógica detrás de CP-007, CP-008, CP-009 (reglas servidor).

---

### 7.2 API — integración (Postman)

**Herramienta:** colección `docs/postman/HappyJump-API.postman_collection.json`

**Pasos:**

1. Importar colección y entorno `HappyJump-local.postman_environment.json`  
2. Ajustar `baseUrl` (ej. `http://localhost:3000`)  
3. Ejecutar carpeta **Smoke** o requests de login + reservas  

**Resultado:** _____ / _____ requests OK  

**Casos relacionados:** CP-006 (health), CP-001/002 (login API), CP-007–011 (endpoints reservas).

---

### 7.3 API — smoke automatizado (k6) — opcional

```powershell
k6 run k6/smoke-test.js
```

Ver `docs/TUTORIAL_K6_HAPPY_JUMP.md`.

---

### 7.4 Android — pruebas unitarias (JVM, no UI E2E)

**Herramienta:** Gradle

```powershell
.\gradlew :app:testDebugUnitTest
```

**Qué valida:** lógica de calendario, grupos de turno, utilidades (`app/src/test/java/...`).

**Nota:** No sustituye prueba manual de pantallas; complementa CP-015, CP-017, CP-018, CP-021.

---

### 7.5 CI — GitHub Actions

Workflow: `.github/workflows/api-tests.yml`  
Disparador: push/PR en `main` / `develop`  

**Evidencia:** captura del workflow en verde en https://github.com/Randy236/apphappy/actions

---

## 8. Matriz manual vs automatizado

| Caso | Descripción breve | Manual (UI) | Automatizado |
|------|-------------------|-------------|--------------|
| CP-001 | Login trabajador | Sí | Postman (API login) |
| CP-006 | Health | Sí (navegador) | Postman / k6 / CI |
| CP-007–008 | Cancha | Sí | Parcial (tests dominio API) |
| CP-010–011 | Salones | Sí | Postman |
| CP-014 | Smoke | Sí | — |
| CP-015–022 | Calendario / turnos | Sí | Unit tests JVM (lógica) |

---

## 9. Defectos encontrados (si aplica)

| ID defecto | Caso CP | Severidad | Descripción | Estado |
|------------|---------|-----------|-------------|--------|
| DEF-001 | | Alta/Media/Baja | | Abierto/Cerrado |

---

## 10. Conclusiones

Se ejecutaron pruebas de **sistema** y **E2E** sobre Happy Jump alineadas al catálogo **CP-001 – CP-022**. Los flujos críticos (login, cancha, salones, health API) se validaron **manualmente** en dispositivo/emulador, garantizando la experiencia de usuario real.

Las pruebas **automatizadas** cubrieron la **API** (`npm run test:ci`, Postman, GitHub Actions) y **lógica Android** en JVM, reduciendo riesgo de regresión en reglas de negocio.

**Recomendaciones:**

- Mantener smoke manual **CP-014** antes de cada entrega.  
- Ampliar automatización UI (Espresso/Compose Test) en futuras iteraciones.  
- Ejecutar Sonar/Snyk como complemento de calidad estática.

---

## 11. Anexos

- A. `CASOS_DE_PRUEBA.csv`  
- B. `RESULTADOS_EJECUCION.csv`  
- C. Capturas en `docs/informes-item6/capturas/`  
- D. Salida `npm run test:ci` / Postman / Actions  
