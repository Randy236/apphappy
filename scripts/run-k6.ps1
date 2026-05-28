# Ejecuta las 3 pruebas k6 (API debe estar en http://localhost:3000)
# Uso: .\scripts\run-k6.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

if (-not (Get-Command k6 -ErrorAction SilentlyContinue)) {
    Write-Host "k6 no instalado. Ejecuta: choco install k6 -y" -ForegroundColor Red
    exit 1
}

$base = if ($env:BASE_URL) { $env:BASE_URL } else { "http://localhost:3000" }
Write-Host "BASE_URL=$base" -ForegroundColor Cyan

foreach ($test in @("load-test.js", "smoke-test.js", "stress-test.js")) {
    Write-Host "`n=== k6 run $test ===" -ForegroundColor Yellow
    k6 run "k6/$test"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Write-Host "`nLas 3 pruebas terminaron OK." -ForegroundColor Green
