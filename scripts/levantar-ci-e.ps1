# Un solo comando: preparar E:, verificar Docker y levantar Sonar + Jenkins
# Uso: .\scripts\levantar-ci-e.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

. "$PSScriptRoot\todo-en-disco-e.ps1"
. "$PSScriptRoot\lib-docker.ps1"

Write-Host "`n=== Verificacion Docker ===" -ForegroundColor Cyan
Invoke-Docker version
Invoke-Docker run --rm hello-world | Select-String "Hello from Docker"

Write-Host "`n=== Levantando stack (Sonar + Jenkins) ===" -ForegroundColor Cyan
Set-Location "$Root\infra\docker"
Invoke-Docker compose pull sonar-db sonarqube
Invoke-Docker compose up -d --build

Write-Host "`nEsperando servicios (puede tardar 3-5 min la primera vez)..." -ForegroundColor Yellow
Start-Sleep -Seconds 15
Invoke-Docker compose ps

Write-Host @"

Listo:
  Jenkins  -> http://localhost:8081
  SonarQube -> http://localhost:9000  (admin / admin, cambiar password)

Si Checkout SCM falla: .\scripts\fix-jenkins-plugins.ps1

"@ -ForegroundColor Green
