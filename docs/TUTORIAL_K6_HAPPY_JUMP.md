# Tutorial — Pruebas de rendimiento con k6 (Happy Jump)

Adaptación del material del profesor al proyecto **Happy Jump** (API Node.js en `server/`).

> El tutorial original usa Java/Tomcat en puerto **8080**. En Happy Jump los endpoints equivalentes están en la API Express (puerto **3000** por defecto): `/hello` y `/sumar`.

---

## I. Herramientas de pruebas de rendimiento

Herramientas comunes: **Apache JMeter**, **k6 (Grafana Labs)**, Gatling, Locust, etc.

## II. Diferencias clave: JMeter vs k6

| Aspecto | JMeter | k6 |
|---------|--------|-----|
| **Lenguaje** | GUI + XML / Groovy | JavaScript |
| **Recursos** | Pesado (Java) | Ligero (Go) |
| **Integración** | Tradicional / enterprise | CI/CD nativo |
| **Protocolos** | FTP, JDBC, SOAP, HTTP… | HTTP/2, gRPC, APIs REST |

## III. ¿Cuándo usar?

- **k6:** desarrollador, CI/CD, APIs/microservicios, infraestructura como código.
- **JMeter:** protocolos antiguos, interfaz visual, entornos corporativos establecidos.

## IV. Tabla comparativa

| Característica | Apache JMeter | k6 |
|----------------|---------------|-----|
| Scripting | GUI, XML, Groovy | JavaScript |
| Arquitectura | Java (pesado) | Go (ligero) |
| Curva de aprendizaje | Alta | Baja (si sabes JS) |
| CI/CD | Moderada | Excelente |
| RAM | Alta | Muy baja |
| Informes | HTML / listeners | JSON, Grafana, k6 Cloud |

Fuente: [appmatics — JMeter vs k6](https://www.appmatics.com/en/blog/jmeter-vs-k6-lasttest-toolvergleich)

---

## Instalación en Windows

### Opción A — winget (recomendada si `choco` no existe)

PowerShell normal (no hace falta Chocolatey):

```powershell
winget install GrafanaLabs.k6 --accept-package-agreements --accept-source-agreements
```

Cierra y abre PowerShell, luego:

```powershell
k6 version
```

### Opción B — Chocolatey (como pide el profesor)

**Paso 1** — PowerShell **como administrador**:

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = `
  [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

**Paso 2** — Cerrar y abrir PowerShell como admin; verificar:

```powershell
choco --version
```

**Paso 3** — Instalar k6:

```powershell
choco install k6 -y
```

**Paso 4** — Verificar:

```powershell
k6 version
```

> Si `choco` no se reconoce, usa la **Opción A (winget)**. Para la entrega puedes anotar: *“Chocolatey no estaba instalado; se usó winget (instalador oficial de k6 v2.0.0).”*

---

## Endpoints de prueba en Happy Jump

En `server/src/index.js` (equivalente al *controller* del tutorial):

| Ruta | Respuesta |
|------|-----------|
| `GET /hello` | `Good Morning` (texto) |
| `GET /sumar?a=5&b=3` | `8` |
| `GET /sumar?a=10&b=20` | `30` |

---

## Paso 5 — Carpeta k6 en la raíz del proyecto

Ya incluida:

```
k6/
  load-test.js
  smoke-test.js
  stress-test.js
```

---

## Paso 6 — Levantar la API antes de probar

En **otra** terminal (MySQL debe estar activo con la BD configurada en `server/.env`):

```powershell
cd D:\apphappy-full\server
npm install
npm start
```

Debe mostrar: `Happy Jump API http://0.0.0.0:3000`

Prueba manual:

```powershell
curl http://localhost:3000/hello
curl "http://localhost:3000/sumar?a=5&b=3"
```

---

## Paso 7–10 — Scripts k6

Los tres archivos en `k6/` siguen la misma lógica del tutorial; solo cambia la URL base (`localhost:3000` en lugar de `8080`).

Opcional — otro puerto:

```powershell
$env:BASE_URL = "http://localhost:8080"
k6 run k6/load-test.js
```

---

## Paso 11 — Ejecutar las 3 pruebas

Desde la **raíz** del repo (`D:\apphappy-full`):

```powershell
k6 run k6/load-test.js
k6 run k6/smoke-test.js
k6 run k6/stress-test.js
```

### Qué mide cada una

| Script | Objetivo |
|--------|----------|
| **load-test** | 1 usuario, 5 s — verifica que `/hello` responde bien |
| **smoke-test** | 20 usuarios, 20 s — carga ligera en `/sumar` |
| **stress-test** | Sube de 10 → 100 VUs en etapas — estrés en `/sumar` |

---

## Entrega al profesor (evidencia)

1. Captura de `k6 version`
2. Captura de la API corriendo (`npm start`)
3. Captura de salida de las **3** pruebas (métricas `http_req_duration`, `checks`, etc.)
4. Este documento + carpeta `k6/`

---

## Solución de problemas

| Error | Causa probable |
|-------|----------------|
| `connection refused` | API no está levantada o puerto distinto |
| `resultado correcto` falla | Revisa query string `a` y `b` |
| k6 no encontrado | Cierra y abre terminal tras `choco install k6` |
