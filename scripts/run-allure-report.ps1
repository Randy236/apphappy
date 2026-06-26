# Ejecuta pruebas unitarias (API + Android) y genera reporte Allure HTML.
# Uso: .\scripts\run-allure-report.ps1
# Requiere: JDK, Android SDK (local.properties), Node en PATH.

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

if (-not (Test-Path "local.properties")) {
    Write-Host "Falta local.properties con sdk.dir" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== 1/3 Tests API (JUnit XML para Allure) ===" -ForegroundColor Cyan
New-Item -ItemType Directory -Force -Path "app\build\test-results\apiUnit" | Out-Null
Push-Location server
npm test 2>&1 | Out-Host
if ($LASTEXITCODE -ne 0) { Pop-Location; exit $LASTEXITCODE }
npm run test:junit 2>&1 | Out-Host
if ($LASTEXITCODE -ne 0) { Pop-Location; exit $LASTEXITCODE }
Pop-Location

Write-Host "`n=== 2/3 Tests Android + informe Allure ===" -ForegroundColor Cyan
.\gradlew.bat --stop 2>$null
$env:GRADLE_OPTS = "-Xmx2560m -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8"
& .\gradlew.bat :app:testDebugUnitTest :app:allureReport --no-daemon

if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$html = Join-Path $Root "app\build\reports\allure-report\allureReport\index.html"
Write-Host "`n=== 3/3 Abrir reporte ===" -ForegroundColor Green
Write-Host @"

NO abras index.html con doble clic (queda en 'Loading...').
Usa uno de estos metodos:

  A) Servidor Allure (recomendado):
     cd D:\apphappy-full
     .\gradlew.bat :app:allureServe

     Se abrira http://localhost:XXXX — deja la ventana PowerShell abierta.

  B) Servidor Python (si A falla):
     cd D:\apphappy-full\app\build\reports\allure-report\allureReport
     python -m http.server 8765
     Abre: http://localhost:8765

Reporte generado en:
  $html

"@ -ForegroundColor Cyan

$openServe = Read-Host "Abrir allureServe ahora? (S/N)"
if ($openServe -eq "S" -or $openServe -eq "s") {
    Write-Host "Iniciando allureServe (Ctrl+C para cerrar)..." -ForegroundColor Yellow
    & .\gradlew.bat :app:allureServe --no-daemon
}
