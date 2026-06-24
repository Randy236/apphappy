# Despliegue API Happy Jump en Railway
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Server = Join-Path $Root "server"

Write-Host "=== Deploy Happy Jump en Railway ===" -ForegroundColor Cyan

# Instalar CLI si falta
if (-not (Get-Command railway -ErrorAction SilentlyContinue)) {
    Write-Host "Instalando Railway CLI..." -ForegroundColor Yellow
    npm install -g @railway/cli
}

Write-Host "`nRailway CLI:" -ForegroundColor Cyan
railway --version

Write-Host @"

PASOS MANUALES (requieren tu cuenta Railway):

1. Abre: https://railway.app
2. New Project → Deploy from GitHub → apphappy
3. Root Directory del servicio: server
4. + New → Database → MySQL
5. En API → Variables → referenciar MySQL + agregar:
     NODE_ENV=production
     JWT_SECRET=[secreto largo aleatorio]
6. Settings → Networking → Generate Domain

O por CLI desde server/:
  cd $Server
  railway login
  railway init
  railway add --database mysql
  railway variables set NODE_ENV=production JWT_SECRET=tu_secreto
  railway up
  railway domain

Guía completa: docs\GUIA_DEPLOY_RAILWAY.md

"@ -ForegroundColor Yellow

# Intentar login status
try {
    Push-Location $Server
    railway whoami 2>&1
} catch {
    Write-Host "No has iniciado sesion. Ejecuta: railway login" -ForegroundColor Red
} finally {
    Pop-Location
}
