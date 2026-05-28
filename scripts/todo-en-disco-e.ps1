# CI en disco E: (Docker + datos pesados fuera de C:)
# Uso: .\scripts\todo-en-disco-e.ps1

$ErrorActionPreference = "Stop"
$CiRoot = "E:\happyjump-ci"
$CodeRoot = if (Test-Path "E:\apphappy") { "E:\apphappy" }
            elseif (Test-Path "D:\apphappy") { "D:\apphappy" }
            else { Split-Path -Parent $PSScriptRoot }

$dirs = @(
    "$CiRoot\.gradle", "$CiRoot\.gradle-jenkins", "$CiRoot\tmp", "$CiRoot\tmp\docker",
    "$CiRoot\data\sonar-db", "$CiRoot\data\sonarqube\data", "$CiRoot\data\sonarqube\extensions",
    "$CiRoot\data\sonarqube\logs", "$CiRoot\data\jenkins_home", "$CiRoot\deploy-runtime"
)
foreach ($d in $dirs) { New-Item -ItemType Directory -Force -Path $d | Out-Null }

[Environment]::SetEnvironmentVariable("GRADLE_USER_HOME", "$CiRoot\.gradle", "User")
[Environment]::SetEnvironmentVariable("NPM_CONFIG_CACHE", "$CiRoot\.npm-cache", "User")
[Environment]::SetEnvironmentVariable("TEMP", "$CiRoot\tmp", "User")
[Environment]::SetEnvironmentVariable("TMP", "$CiRoot\tmp", "User")
[Environment]::SetEnvironmentVariable("DOCKER_TMPDIR", "$CiRoot\tmp\docker", "User")
$env:GRADLE_USER_HOME = "$CiRoot\.gradle"
$env:NPM_CONFIG_CACHE = "$CiRoot\.npm-cache"
$env:TEMP = "$CiRoot\tmp"
$env:TMP = "$CiRoot\tmp"
$env:DOCKER_TMPDIR = "$CiRoot\tmp\docker"

$c = [math]::Round((Get-PSDrive C -EA SilentlyContinue).Free / 1GB, 1)
$e = [math]::Round((Get-PSDrive E -EA SilentlyContinue).Free / 1GB, 1)
Write-Host "OK - CI en $CiRoot | Codigo: $CodeRoot" -ForegroundColor Green
Write-Host "C: $c GB libre | E: $e GB libre" -ForegroundColor Cyan
Write-Host "Docker datos: E:\DockerDesktop (verifica en Docker Desktop Settings)" -ForegroundColor Yellow
