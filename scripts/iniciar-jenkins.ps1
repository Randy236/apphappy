# Inicia Docker (si hace falta) + Jenkins en http://localhost:8081
# Ejecutar como ADMINISTRADOR: clic derecho PowerShell -> Ejecutar como administrador
#   cd D:\apphappy-full
#   .\scripts\iniciar-jenkins.ps1

$ErrorActionPreference = "Continue"
$Root = Split-Path -Parent $PSScriptRoot
$ComposeDir = Join-Path $Root "infra\docker"
$DockerDesktop = "E:\docker\Program\Docker Desktop.exe"
$DockerBin = "E:\docker\Program\resources\bin\docker.exe"

if (-not (Test-Path $DockerBin)) {
    Write-Host "No se encuentra Docker en E:\docker\Program" -ForegroundColor Red
    exit 1
}

function Test-DockerEngine {
    $v = & $DockerBin version --format "{{.Server.Version}}" 2>$null
    return ($LASTEXITCODE -eq 0 -and $v)
}

function Wait-DockerEngine {
    param([int]$MaxMinutes = 10)
    $max = $MaxMinutes * 6
    for ($i = 1; $i -le $max; $i++) {
        if (Test-DockerEngine) { return $true }
        Start-Sleep -Seconds 10
        if ($i % 6 -eq 0) { Write-Host "  Esperando Docker... ($([int]($i/6)) min)" -ForegroundColor Yellow }
    }
    return $false
}

function Test-PortOpen([int]$Port) {
    try {
        $t = New-Object System.Net.Sockets.TcpClient
        $r = $t.BeginConnect("127.0.0.1", $Port, $null, $null)
        $ok = $r.AsyncWaitHandle.WaitOne(2000, $false)
        $t.Close()
        return $ok
    } catch { return $false }
}

Write-Host "`n=== Happy Jump: iniciar Jenkins ===" -ForegroundColor Cyan

# Carpetas locales (no dependen de E:)
$data = Join-Path $ComposeDir "data\jenkins_home"
New-Item -ItemType Directory -Force -Path $data | Out-Null

if (-not (Test-DockerEngine)) {
    Write-Host "Docker no responde. Intentando arrancar Docker Desktop..." -ForegroundColor Yellow
    if (Test-Path $DockerDesktop) {
        Start-Process $DockerDesktop
    } else {
        Write-Host "Abre Docker Desktop manualmente desde el menu Inicio." -ForegroundColor Red
    }
    if (-not (Wait-DockerEngine)) {
        Write-Host @"

FALLO: el motor Docker no arranco.

Haz esto (en orden):
  1) Reinicia Windows
  2) Abre Docker Desktop y espera 'Engine running' (barra verde)
  3) Vuelve a ejecutar este script COMO ADMINISTRADOR

"@ -ForegroundColor Red
        exit 1
    }
}

Write-Host "Docker OK" -ForegroundColor Green

Set-Location $ComposeDir

# Quitar contenedores viejos colgados del proyecto anterior
& $DockerBin rm -f happyjump-ci-jenkins-1 happyjump-jenkins 2>$null
& $DockerBin compose -f docker-compose.jenkins-only.yml down --remove-orphans 2>$null

Write-Host "Levantando Jenkins (solo, mas rapido)..." -ForegroundColor Cyan
& $DockerBin compose -f docker-compose.jenkins-only.yml pull jenkins 2>&1
& $DockerBin compose -f docker-compose.jenkins-only.yml up -d

Write-Host "Esperando que Jenkins escuche en 8081..." -ForegroundColor Yellow
$ready = $false
for ($i = 1; $i -le 36; $i++) {
    Start-Sleep -Seconds 5
    if (Test-PortOpen 8081) {
        $ready = $true
        Write-Host "Puerto 8081 ABIERTO (intento $i)" -ForegroundColor Green
        break
    }
    if ($i % 6 -eq 0) { Write-Host "  ... aun iniciando Jenkins" }
}

& $DockerBin compose -f docker-compose.jenkins-only.yml ps

$pass = ""
try {
    $pass = & $DockerBin exec happyjump-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>$null
} catch { }

if ($ready) {
    if ($pass) {
        Write-Host "`nContrasena desbloqueo Jenkins:`n  $pass`n" -ForegroundColor Green
    }
    Write-Host "Abre en el navegador:  http://localhost:8081`n" -ForegroundColor Green
    Start-Process "http://localhost:8081"
} else {
    Write-Host @"

Jenkins aun no responde en 8081.
Mira logs:
  cd D:\apphappy-full\infra\docker
  docker compose -f docker-compose.jenkins-only.yml logs -f jenkins

Espera 2 min mas y prueba http://localhost:8081 de nuevo.

"@ -ForegroundColor Yellow
}
