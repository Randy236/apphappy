# Cobertura local (JaCoCo) — evita daemon colgado y usa mas RAM
# Uso: .\scripts\run-coverage-local.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

if (-not (Test-Path "local.properties")) {
    Write-Host "Falta local.properties con sdk.dir. Copia local.properties.example" -ForegroundColor Red
    exit 1
}

Write-Host "Deteniendo daemons Gradle viejos..." -ForegroundColor Cyan
.\gradlew.bat --stop 2>$null

Write-Host "Ejecutando tests + JaCoCo (sin daemon, ~3-5 min)..." -ForegroundColor Cyan
$env:GRADLE_OPTS = "-Xmx2560m -XX:MaxMetaspaceSize=512m -Xss1m -Dfile.encoding=UTF-8"
& .\gradlew.bat :app:testDebugUnitTest :app:jacocoTestReport :app:printJacocoTotals --no-daemon

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nSi falla por memoria: cierra Docker/Chrome y usa Android Studio -> Run with Coverage" -ForegroundColor Yellow
    exit $LASTEXITCODE
}

$html = "$Root\app\build\reports\jacoco\jacocoTestReport\html\index.html"
Write-Host "`nOK. Abre en el navegador:" -ForegroundColor Green
Write-Host $html
