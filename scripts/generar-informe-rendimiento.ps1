# Entregable 12 — Pruebas de rendimiento (k6) + informe Word
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Results = Join-Path $Root "k6\results"
$Reports = Join-Path $Root "scripts\k6_reports"
$Entregable = Join-Path $Root "entregableunidad\entregable-12"
$LogFile = Join-Path $Results "performance-latest.txt"
$SummaryJson = Join-Path $Results "performance-summary.json"
$ServidorJson = Join-Path $Results "servidor-info.json"
$BaseUrl = if ($env:K6_BASE_URL) { $env:K6_BASE_URL } else { "http://localhost:3000" }

Write-Host "=== Happy Jump — Pruebas de rendimiento (k6) ===" -ForegroundColor Cyan

# 1) API activa
try {
    $h = Invoke-RestMethod -Uri "$BaseUrl/health" -TimeoutSec 5
    if (-not $h.ok) { throw "health no ok" }
    Write-Host "API OK: $BaseUrl" -ForegroundColor Green
} catch {
    Write-Host "La API no responde en $BaseUrl" -ForegroundColor Red
    Write-Host "  cd server && npm start" -ForegroundColor Yellow
    exit 1
}

# 2) Liberar sesiones
Write-Host "Liberando sesiones..." -ForegroundColor DarkGray
npm --prefix (Join-Path $Root "server") run k6:prepare 2>$null

# 3) Recopilar características del servidor
New-Item -ItemType Directory -Force -Path $Results | Out-Null
$nodeVer = try { (node --version 2>$null).Trim() } catch { "" }
$k6Ver = try { (k6 version 2>$null | Select-Object -First 1).Trim() } catch { "" }
$ci = Get-CimInstance Win32_ComputerSystem -ErrorAction SilentlyContinue
$cpu = Get-CimInstance Win32_Processor -ErrorAction SilentlyContinue | Select-Object -First 1
$ramGb = if ($ci) { [math]::Round($ci.TotalPhysicalMemory / 1GB, 1) } else { "" }
$servidor = @{
    hostname = if ($ci) { $ci.Name } else { $env:COMPUTERNAME }
    sistema_operativo = "Windows"
    procesador = if ($cpu) { $cpu.Name.Trim() } else { "" }
    nucleos = if ($ci) { [string]$ci.NumberOfLogicalProcessors } else { "" }
    ram_gb = [string]$ramGb
    node_version = $nodeVer
    k6_version = $k6Ver
    base_url = $BaseUrl
    puerto_api = "3000"
    motor_bd = "MySQL 8 (Laragon)"
    framework_api = "Node.js + Express"
    arquitectura = "Cliente Android + API REST monolitica + MySQL"
}
$servidor | ConvertTo-Json | Out-File $ServidorJson -Encoding utf8NoBOM
Write-Host "Servidor documentado en $ServidorJson" -ForegroundColor DarkGray

# 4) Ejecutar k6 (~5 min: smoke + load + stress)
Write-Host "`nEjecutando k6 performance-api.js (~5 minutos)..." -ForegroundColor Yellow
$env:BASE_URL = $BaseUrl
Push-Location $Root
k6 run --summary-export $SummaryJson k6/performance-api.js 2>&1 | Tee-Object -FilePath $LogFile
$k6Exit = $LASTEXITCODE
Pop-Location

if ($k6Exit -ne 0) {
    Write-Host "`nADVERTENCIA: k6 terminó con código $k6Exit (puede ser umbral). Se genera informe igual." -ForegroundColor Yellow
}

# 5) Generar Word y Excel
Write-Host "`nGenerando informe Word y Excel..." -ForegroundColor Cyan
Push-Location $Reports
python -m pip install -q -r requirements.txt
python run_informe_rendimiento.py
$pyExit = $LASTEXITCODE
Pop-Location
if ($pyExit -ne 0) { exit $pyExit }

# 6) Copiar a entregable-12
New-Item -ItemType Directory -Force -Path $Entregable | Out-Null
$src = Join-Path $Reports "reportes"
Copy-Item "$src\INFORME_PRUEBAS_RENDIMIENTO.docx" $Entregable -Force
Copy-Item "$src\METRICAS_RENDIMIENTO.xlsx" $Entregable -Force
Copy-Item $LogFile $Entregable -Force
Copy-Item $SummaryJson $Entregable -Force -ErrorAction SilentlyContinue
Copy-Item $ServidorJson $Entregable -Force

Write-Host "`nListo — entregableunidad\entregable-12\" -ForegroundColor Green
Get-ChildItem $Entregable | Format-Table Name, Length -AutoSize
