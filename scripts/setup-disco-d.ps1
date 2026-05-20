# Usa disco D: para caches del proyecto y prepara carpetas Docker Compose.
# El motor de Docker Desktop (capas de imagenes) sigue en C: hasta que lo muevas en la UI.
# Uso: .\scripts\setup-disco-d.ps1

$ErrorActionPreference = "Stop"
$Root = "D:\apphappy"
if (-not (Test-Path $Root)) {
    $Root = Split-Path -Parent $PSScriptRoot
}

$dirs = @(
    "$Root\.gradle",
    "$Root\.gradle-jenkins",
    "$Root\.gradle-preflight",
    "$Root\.gradle-ci",
    "$Root\.npm-cache",
    "$Root\tmp",
    "$Root\infra\docker\data\sonar-db",
    "$Root\infra\docker\data\sonarqube\data",
    "$Root\infra\docker\data\sonarqube\extensions",
    "$Root\infra\docker\data\sonarqube\logs",
    "$Root\infra\docker\data\jenkins_home",
    "$Root\deploy-runtime"
)

foreach ($d in $dirs) {
    New-Item -ItemType Directory -Force -Path $d | Out-Null
}

# Variables para esta sesion y futuras (usuario)
[Environment]::SetEnvironmentVariable("GRADLE_USER_HOME", "$Root\.gradle", "User")
[Environment]::SetEnvironmentVariable("NPM_CONFIG_CACHE", "$Root\.npm-cache", "User")
[Environment]::SetEnvironmentVariable("TEMP", "$Root\tmp", "User")
[Environment]::SetEnvironmentVariable("TMP", "$Root\tmp", "User")

$env:GRADLE_USER_HOME = "$Root\.gradle"
$env:NPM_CONFIG_CACHE = "$Root\.npm-cache"
$env:TEMP = "$Root\tmp"
$env:TMP = "$Root\tmp"

Write-Host "Carpetas en D: listas." -ForegroundColor Green
Write-Host "  GRADLE_USER_HOME = $env:GRADLE_USER_HOME"
Write-Host "  NPM_CONFIG_CACHE = $env:NPM_CONFIG_CACHE"
Write-Host "  Docker Compose data -> $Root\infra\docker\data\"

$cFree = (Get-Volume -DriveLetter C -ErrorAction SilentlyContinue).SizeRemaining / 1GB
$dFree = (Get-Volume -DriveLetter D -ErrorAction SilentlyContinue).SizeRemaining / 1GB
Write-Host ""
Write-Host ("Espacio libre C: {0:N1} GB | D: {1:N1} GB" -f $cFree, $dFree) -ForegroundColor Cyan

if ($cFree -lt 10) {
    Write-Host ""
    Write-Host "IMPORTANTE: C: tiene poco espacio. Mueve el disco de Docker Desktop a D:" -ForegroundColor Yellow
    Write-Host "  1. Cierra Docker Desktop (icono bandeja -> Quit)"
    Write-Host "  2. Abre Docker Desktop -> Settings -> Resources -> Advanced"
    Write-Host "  3. Disk image location -> D:\DockerDesktop"
    Write-Host "  4. Apply & Restart"
    Write-Host ""
    Write-Host "Sin eso, crear contenedores puede seguir fallando aunque los datos del compose esten en D:\apphappy."
}

Write-Host ""
Write-Host "Siguiente:" -ForegroundColor Green
Write-Host "  cd $Root\infra\docker"
Write-Host "  docker compose up -d"
