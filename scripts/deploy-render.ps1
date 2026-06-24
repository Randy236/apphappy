# Ayuda despliegue Render + db4free
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot

Write-Host @"
╔══════════════════════════════════════════════════════════╗
║  Happy Jump — Deploy GRATIS en Render                    ║
╚══════════════════════════════════════════════════════════╝

PASO 1 — MySQL gratis
  https://www.db4free.net/signup.php
  Anota: DB_HOST, DB_USER, DB_PASSWORD, DB_NAME

PASO 2 — Render
  https://dashboard.render.com
  New + → Blueprint → repo apphappy (detecta render.yaml)
  O: Web Service → rootDir: server → start: npm run start:cloud

PASO 3 — Variables en Render (Environment)
  NODE_ENV=production
  JWT_SECRET=HappyJumpRender2026SecretoLargo
  DB_HOST=db4free.net
  DB_PORT=3306
  DB_USER=...
  DB_PASSWORD=...
  DB_NAME=...
  DB_SSL=false

PASO 4 — Probar URL
  https://TU-APP.onrender.com/health
  https://TU-APP.onrender.com/swagger-ui/

PASO 5 — Android local.properties
  happyJump.api.baseUrl=https://TU-APP.onrender.com/

Guía completa: docs\GUIA_DEPLOY_RENDER.md

"@ -ForegroundColor Cyan

# Probar URL si el usuario la pasa como argumento
if ($args.Count -gt 0) {
    $url = $args[0].TrimEnd('/')
    Write-Host "Probando $url/health ..." -ForegroundColor Yellow
    try {
        $r = Invoke-RestMethod "$url/health" -TimeoutSec 90
        Write-Host "OK: $($r | ConvertTo-Json -Compress)" -ForegroundColor Green
    } catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "(Si plan Free, la 1ra petición puede tardar 60s)" -ForegroundColor DarkYellow
    }
}
