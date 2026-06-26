# Escaneo y reporte ZAP usando ZAP Desktop 2.17 (sin Docker)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$ZapDir = Join-Path $Root "zap"
$Entregable = Join-Path $Root "entregableunidad\entregable-08"
$OpenApi = Join-Path $Root "server\openapi.json"
$Target = if ($env:ZAP_TARGET) { $env:ZAP_TARGET } else { "http://localhost:3000" }
$ZapPort = if ($env:ZAP_PORT) { $env:ZAP_PORT } else { "8080" }

Write-Host "=== ZAP Desktop - Happy Jump ===" -ForegroundColor Cyan

try {
    Invoke-RestMethod "$Target/health" -TimeoutSec 5 | Out-Null
    Write-Host "API OK: $Target" -ForegroundColor Green
} catch {
    Write-Host "La API no responde en $Target. Ejecuta: cd server && npm start" -ForegroundColor Red
    exit 1
}

$configPath = Join-Path $env:USERPROFILE "ZAP\config.xml"
if (-not (Test-Path $configPath)) {
    Write-Host "Abre OWASP ZAP Desktop 2.17 y vuelve a ejecutar este script." -ForegroundColor Red
    exit 1
}
$key = ([xml](Get-Content $configPath)).config.api.key
if (-not $key) {
    Write-Host "No se encontro la API key en $configPath" -ForegroundColor Red
    exit 1
}

$base = "http://127.0.0.1:$ZapPort"
try {
    $ver = (Invoke-RestMethod "$base/JSON/core/view/version/?apikey=$key").version
    Write-Host "ZAP Desktop OK: $ver (puerto $ZapPort)" -ForegroundColor Green
} catch {
    Write-Host "ZAP no responde en puerto $ZapPort. Abre ZAP Desktop (debe estar encendido)." -ForegroundColor Red
    exit 1
}

$clear = Join-Path $Root "server\scripts\clear-sessions-for-k6.mjs"
if (Test-Path $clear) {
    Push-Location (Join-Path $Root "server")
    node scripts/clear-sessions-for-k6.mjs 2>$null
    Pop-Location
}

$sites = (Invoke-RestMethod "$base/JSON/core/view/sites/?apikey=$key").sites
if ($sites -notcontains $Target) {
    Write-Host "Importando OpenAPI..." -ForegroundColor Yellow
    $fileEnc = [uri]::EscapeDataString($OpenApi)
    Invoke-RestMethod "$base/JSON/openapi/action/importFile/?apikey=$key&file=$fileEnc&target=$([uri]::EscapeDataString($Target))" | Out-Null
    Start-Sleep -Seconds 3
}

$summary = Invoke-RestMethod "$base/JSON/core/view/alertsSummary/?apikey=$key"
$total = [int]$summary.alertsSummary.High + [int]$summary.alertsSummary.Medium + [int]$summary.alertsSummary.Low + [int]$summary.alertsSummary.Informational
if ($total -lt 5) {
    Write-Host "Iniciando escaneo activo (5-15 min)..." -ForegroundColor Yellow
    $scan = Invoke-RestMethod "$base/JSON/ascan/action/scan/?apikey=$key&url=$([uri]::EscapeDataString($Target))&recurse=true&inScopeOnly=false"
    $scanId = $scan.scan
    do {
        Start-Sleep -Seconds 10
        $pct = (Invoke-RestMethod "$base/JSON/ascan/view/status/?apikey=$key&scanId=$scanId").status
        Write-Host "  Progreso: $pct%"
    } while ([int]$pct -lt 100)
} else {
    Write-Host "Ya hay alertas en ZAP ($total). Generando reporte..." -ForegroundColor DarkGray
}

New-Item -ItemType Directory -Force -Path $ZapDir, $Entregable | Out-Null
$htmlName = "ZAP-Report-HappyJump"
$params = @{
    apikey = $key
    title = "Happy Jump API"
    template = "risk-confidence-html"
    theme = "original"
    description = "Pruebas de seguridad DAST - Happy Jump API"
    sites = $Target
    reportDir = $Entregable
    reportFileName = $htmlName
    display = "false"
}
$qs = ($params.GetEnumerator() | ForEach-Object { "$($_.Key)=$([uri]::EscapeDataString([string]$_.Value))" }) -join "&"
$gen = Invoke-RestMethod "$base/JSON/reports/action/generate/?$qs" -TimeoutSec 180
Write-Host "HTML: $($gen.generate)" -ForegroundColor Green

$jsonParams = @{
    apikey = $key
    title = "Happy Jump API"
    template = "traditional-json"
    sites = $Target
    reportDir = $ZapDir
    reportFileName = "zap-report"
    display = "false"
}
$qs2 = ($jsonParams.GetEnumerator() | ForEach-Object { "$($_.Key)=$([uri]::EscapeDataString([string]$_.Value))" }) -join "&"
Invoke-RestMethod "$base/JSON/reports/action/generate/?$qs2" -TimeoutSec 120 | Out-Null

Write-Host ""
Write-Host "Siguiente: .\scripts\generar-informe-seguridad-zap.ps1" -ForegroundColor Cyan
