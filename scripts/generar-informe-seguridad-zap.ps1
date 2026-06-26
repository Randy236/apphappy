# ZAP + Word + Excel (entregable 8)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$ZapDir = Join-Path $Root "zap"
$Reports = Join-Path $Root "scripts\zap_reports"
$Entregable = Join-Path $Root "entregableunidad\entregable-08"

if (-not (Test-Path "$ZapDir\zap-report.json")) {
    Write-Host "Ejecutando escaneo OWASP ZAP..." -ForegroundColor Cyan
    & "$Root\scripts\run-zap-seguridad.ps1"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Write-Host "Generando informes Word y Excel..." -ForegroundColor Cyan
Push-Location $Reports
python -m pip install -q -r requirements.txt
python run_informe_zap.py
$code = $LASTEXITCODE
Pop-Location
if ($code -ne 0) { exit $code }

New-Item -ItemType Directory -Force -Path $Entregable | Out-Null
$src = Join-Path $Reports "reportes"
Copy-Item "$src\INFORME_PRUEBAS_SEGURIDAD_ZAP.docx" $Entregable -Force -ErrorAction SilentlyContinue
Copy-Item "$src\RESULTADOS_SEGURIDAD_ZAP.xlsx" $Entregable -Force -ErrorAction SilentlyContinue
Copy-Item "$ZapDir\zap-report.html" $Entregable -Force -ErrorAction SilentlyContinue

Write-Host "`nCopiados a entregableunidad\entregable-08\" -ForegroundColor Green
