# Ejecuta localmente lo mismo que el pipeline Jenkins (sin Jenkins)
# Uso: .\scripts\ci-local.ps1
# Requisito: Docker Desktop en ejecución

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

Write-Host "=== 1. Pruebas API (Node) ===" -ForegroundColor Cyan
Push-Location server
npm ci 2>$null; if (-not $?) { npm install }
npm test
Pop-Location

Write-Host "=== 2. Pruebas Android + JaCoCo (Docker) ===" -ForegroundColor Cyan
$androidImage = "mingc/android-build-box:latest"
docker pull $androidImage
docker run --rm -v "${Root}:/project" -w /project $androidImage `
  bash -lc "chmod +x gradlew && ./gradlew :app:testDebugUnitTest :app:jacocoTestReport --no-daemon"

Write-Host "=== 3. SonarQube local (opcional) ===" -ForegroundColor Cyan
if ($env:SONAR_TOKEN) {
  docker run --rm -v "${Root}:/project" -w /project -e SONAR_TOKEN $androidImage `
    bash -lc "chmod +x gradlew && ./gradlew sonar -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.token=$env:SONAR_TOKEN -Dsonar.projectKey=happyjump-local --no-daemon"
} else {
  Write-Host "Omitido: define SONAR_TOKEN para enviar a SonarQube en localhost:9000" -ForegroundColor Yellow
}

Write-Host "=== Listo ===" -ForegroundColor Green
Write-Host "JaCoCo HTML: app\build\reports\jacoco\jacocoTestReport\html\index.html"
