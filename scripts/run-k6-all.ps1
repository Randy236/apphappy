# Prepara sesiones + ejecuta k6 en todas las rutas
# Uso: .\scripts\run-k6-all.ps1
# Requiere: API en http://localhost:3000 (npm start en server/)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

Write-Host "1) Liberando sesiones en MySQL..." -ForegroundColor Cyan
npm --prefix server run k6:prepare
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n2) Comprobando API /health..." -ForegroundColor Cyan
try {
  $h = Invoke-RestMethod -Uri "http://localhost:3000/health" -TimeoutSec 5
  if (-not $h.ok) { throw "health no ok" }
} catch {
  Write-Host "La API no responde en :3000. En otra ventana: cd server; npm start" -ForegroundColor Red
  exit 1
}

Write-Host "`n3) k6 — todas las rutas..." -ForegroundColor Cyan
k6 run k6/api-all-endpoints.js
exit $LASTEXITCODE
