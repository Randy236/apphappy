# Ítem 3 — Captura SonarQube + cobertura pruebas unitarias ≥ 80%

## Qué pide el profesor

1. **Captura de pantalla** del tablero **SonarQube** (o SonarCloud, es el mismo producto en la nube).
2. Que se vea **cobertura de pruebas unitarias ≥ 80%**.

En Happy Jump la cobertura sale de:
- Tests Android (JUnit) en `app/src/test/`
- Informe **JaCoCo** → subido a Sonar

---

## Opción A — SonarCloud (recomendada, ya configurada)

### Paso 1 — Verificar secreto en GitHub

1. https://github.com/Randy236/apphappy → **Settings** → **Secrets and variables** → **Actions**
2. Debe existir **`SONAR_TOKEN`** (token de https://sonarcloud.io → My Account → Security)

Si no existe, créalo y vuelve a ejecutar el workflow.

### Paso 2 — Ejecutar análisis en CI

1. GitHub → pestaña **Actions**
2. Workflow **SonarCloud**
3. Si no hay run reciente: **Run workflow** (o haz un `git push` a `main`)

Debe terminar en **verde** (tests + jacoco + sonar).

### Paso 3 — Abrir el proyecto en SonarCloud

1. Entra a https://sonarcloud.io  
2. Inicia sesión con GitHub  
3. Abre el proyecto **`Randy236_apphappy`** (o *apphappy*)

### Paso 4 — Captura para el ítem 3

Toma captura donde se vea claramente:

| Dónde en SonarCloud | Qué debe verse |
|---------------------|----------------|
| **Overview** (resumen) | Métrica **Coverage** (cobertura) |
| O **Measures** → **Coverage** | Porcentaje **≥ 80%** |

**Tip:** Si la cobertura **global** es menor a 80%, revisa también:
- **Coverage on New Code** (código nuevo) — a veces el curso acepta esta métrica.
- Pregunta al profesor si pide **líneas** o **código nuevo**.

Guarda la imagen en:

```
D:\apphappy-full\docs\evidencias-item3\
```

Nombre sugerido: `sonarcloud-cobertura-80.png`

### Paso 5 — Captura del workflow (complemento)

GitHub Actions → último **SonarCloud** exitoso → paso **Pruebas unitarias + informe JaCoCo**.

Muestra que las unitarias corrieron antes del análisis.

---

## Opción B — SonarQube local (Docker, puerto 9000)

Si el profesor exige literalmente “Sonar**Qube**” en servidor propio:

```powershell
cd D:\apphappy-full\infra\docker
docker compose up -d
```

Espera 3–5 min → http://localhost:9000 (admin / admin)

Luego en tu PC (con Android SDK configurado):

```powershell
cd D:\apphappy-full
.\gradlew.bat :app:testDebugUnitTest :app:jacocoTestReport sonar -Dproject.settings=sonar-project.local.properties
```

(Necesitas token Sonar local en `sonar-project.local.properties`)

Captura la pantalla del proyecto **happyjump-local** con Coverage ≥ 80%.

---

## Opción C — JaCoCo local (respaldo si no llega Sonar)

Si tienes **Android SDK** en `local.properties`:

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

Ejecuta:

```powershell
cd D:\apphappy-full
.\gradlew.bat :app:testDebugUnitTest :app:jacocoTestReport :app:printJacocoTotals --no-daemon
```

Abre en el navegador:

```
D:\apphappy-full\app\build\reports\jacoco\jacocoTestReport\html\index.html
```

Captura el **% total de líneas** (instrucciones).

Informe en español (si existe):

```powershell
.\gradlew.bat :app:jacocoReportEspanol
```

Abre: `app\build\reports\jacoco\cobertura-es\tabla-lineas-funciones.html`

---

## Texto para el informe (copiar)

> **Ítem 3 — SonarQube / cobertura unitaria**  
> Se integró SonarCloud al repositorio (`sonar-project.properties`, workflow `sonarcloud.yml`).  
> Las pruebas unitarias Android (`app/src/test/`) generan cobertura JaCoCo exportada a Sonar.  
> Evidencia: captura del dashboard con **Coverage ≥ 80%** y ejecución exitosa del pipeline SonarCloud en GitHub Actions.

---

## Checklist ítem 3

```
[ ] SONAR_TOKEN configurado en GitHub
[ ] Workflow SonarCloud en verde
[ ] Captura SonarCloud con Coverage visible (≥ 80%)
[ ] (Opcional) Captura JaCoCo HTML local
[ ] Guardar en docs/evidencias-item3/
[ ] Push si el profesor pide verlo en el repo
```

---

## "Coverage — No data available" (tu caso)

Eso significa que SonarCloud **sí analizó el código**, pero **no recibió el reporte JaCoCo** de las pruebas unitarias.

Causas habituales:

1. El workflow **SonarCloud en GitHub falla** en el paso *Análisis SonarCloud* (falta `SONAR_TOKEN`).
2. El análisis que ves es **automático** (sin CI); el banner *"CI-based analysis"* lo indica.
3. Ruta del XML JaCoCo incorrecta (corregido en `sonar-project.properties`).

**Qué hacer:**

1. Crea `SONAR_TOKEN` en GitHub Secrets (token de sonarcloud.io).
2. **Actions** → **SonarCloud** → **Run workflow** → debe quedar **verde**.
3. Espera 2–5 min y **refresca** SonarCloud → Overview → debe aparecer **Coverage** con %.
4. Si sigue vacío: abre el run verde → log del paso *Verificar JaCoCo* y *printJacocoTotals*.

---

## Si la cobertura sale por debajo de 80%

1. Ejecuta `printJacocoTotals` y mira qué paquetes tienen 0%.  
2. Las pantallas Compose suelen tener poca cobertura; los tests están en **util** (TimeUtil, Cancha, etc.).  
3. Para subir %: agregar tests en `app/src/test/` sobre clases con lógica pura.  
4. Confirma con el profesor si cuenta **solo módulo app** o también `server/` (Node tests no entran en JaCoCo Android por defecto).

Cuando tengas la captura, di **"listo el 3"** y pasamos al **ítem 4** (informe Sonar Excel/Word).
