# Escaneo OWASP ZAP de la API Happy Jump (OpenAPI + Docker)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$ZapDir = Join-Path $Root "zap"
$ApiUrl = if ($env:ZAP_TARGET) { $env:ZAP_TARGET } else { "http://host.docker.internal:3000" }
$OpenApiSrc = Join-Path $Root "server\openapi.json"
$ClearScript = Join-Path $Root "server\scripts\clear-sessions-for-k6.mjs"

Write-Host "=== OWASP ZAP - Pruebas de seguridad API ===" -ForegroundColor Cyan

try {
    $health = Invoke-RestMethod -Uri "http://localhost:3000/health" -TimeoutSec 5
    Write-Host "API OK: $($health | ConvertTo-Json -Compress)" -ForegroundColor Green
} catch {
    Write-Host "La API no responde en http://localhost:3000" -ForegroundColor Red
    Write-Host "  cd D:\apphappy-full\server" -ForegroundColor Yellow
    Write-Host "  npm start" -ForegroundColor Yellow
    exit 1
}

if (Test-Path $ClearScript) {
    Write-Host "Liberando sesiones activas..." -ForegroundColor DarkGray
    Push-Location (Join-Path $Root "server")
    node scripts/clear-sessions-for-k6.mjs 2>$null
    Pop-Location
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Falta Docker Desktop. Instalalo para ejecutar OWASP ZAP." -ForegroundColor Red
    exit 1
}

New-Item -ItemType Directory -Force -Path $ZapDir | Out-Null
Copy-Item $OpenApiSrc (Join-Path $ZapDir "openapi.json") -Force

Write-Host "Objetivo ZAP: $ApiUrl" -ForegroundColor Cyan
Write-Host "OpenAPI: zap\openapi.json" -ForegroundColor DarkGray
Write-Host "Esto tarda 3-8 minutos (descarga imagen la primera vez)..." -ForegroundColor Yellow

$logFile = Join-Path $ZapDir "zap-scan.log"
docker run --rm `
    -v "${ZapDir}:/zap/wrk:rw" `
    ghcr.io/zaproxy/zaproxy:stable `
    zap-api-scan.py `
    -t $ApiUrl `
    -f openapi `
    -d /zap/wrk/openapi.json `
    -r /zap/wrk/zap-report.html `
    -J /zap/wrk/zap-report.json `
    -w /zap/wrk/zap-report.md `
    2>&1 | Tee-Object -FilePath $logFile

$zapExit = $LASTEXITCODE
if ($zapExit -eq 1) {
    Write-Host "`nERROR en el escaneo ZAP. Ver $logFile" -ForegroundColor Red
    Get-Content $logFile -Tail 20
    exit 1
}

$jsonReport = Join-Path $ZapDir "zap-report.json"
$htmlReport = Join-Path $ZapDir "zap-report.html"
$htmlEntregable = Join-Path $Root "entregableunidad\entregable-08\ZAP-Report-HappyJump.html"
if (-not (Test-Path $jsonReport)) {
    Write-Host "No se genero zap-report.json. Ver $logFile" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "ZAP terminado (exit $zapExit)." -ForegroundColor Green
Write-Host "  $htmlReport"
Write-Host "  $jsonReport"
if (Test-Path $htmlReport) {
    New-Item -ItemType Directory -Force -Path (Split-Path $htmlEntregable) | Out-Null
    Copy-Item $htmlReport $htmlEntregable -Force
    Write-Host "  Copiado a: $htmlEntregable" -ForegroundColor Green
}
Write-Host ""
Write-Host "NOTA: Para el formato exacto del salon (Reporte DAST 2.17), usa ZAP Desktop." -ForegroundColor Yellow
Write-Host "Ver: entregableunidad\entregable-08\COMO_EJECUTAR_ZAP_DESKTOP.txt" -ForegroundColor Yellow
Write-Host ""
Write-Host "Siguiente: .\scripts\generar-informe-seguridad-zap.ps1" -ForegroundColor Cyan
exit 0
