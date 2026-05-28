# Repara Jenkins cuando fallan plugins o Checkout SCM.
# Uso: .\scripts\fix-jenkins-plugins.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\lib-docker.ps1"
. "$PSScriptRoot\todo-en-disco-e.ps1"

$jenkinsData = "E:\happyjump-ci\data\jenkins_home"
$pluginsDir = Join-Path $jenkinsData "plugins"

Write-Host "Deteniendo Jenkins..." -ForegroundColor Cyan
Set-Location "$PSScriptRoot\..\infra\docker"
Invoke-Docker compose stop jenkins 2>$null

if (Test-Path $pluginsDir) {
    $bak = "$pluginsDir.bak-$(Get-Date -Format yyyyMMdd-HHmmss)"
    Write-Host "Respaldando plugins en $bak" -ForegroundColor Yellow
    Move-Item $pluginsDir $bak -Force
}

Write-Host "Reconstruyendo imagen Jenkins..." -ForegroundColor Cyan
Invoke-Docker compose build --no-cache jenkins
Invoke-Docker compose up -d jenkins

Write-Host "Listo. Espera 2-3 min y abre http://localhost:8081" -ForegroundColor Green
