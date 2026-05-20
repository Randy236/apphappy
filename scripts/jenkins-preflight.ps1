# Simula el Jenkinsfile en Windows (sin UI de Jenkins).
# Uso: .\scripts\jenkins-preflight.ps1
# Requisito: Docker Desktop. Opcional: $env:SONAR_TOKEN para etapa Sonar.

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
& "$PSScriptRoot\setup-disco-d.ps1" | Out-Null
Set-Location $Root
$env:GRADLE_USER_HOME = "$Root\.gradle-preflight"
$env:NPM_CONFIG_CACHE = "$Root\.npm-cache"
$env:TEMP = "$Root\tmp"
$env:TMP = "$Root\tmp"
$androidImage = "mingc/android-build-box:latest"

function Invoke-BashScript {
    param([string]$ScriptPath)
    $bash = Get-Command bash -ErrorAction SilentlyContinue
    if ($bash) {
        & bash $ScriptPath
        return
    }
    Write-Host "bash no encontrado; ejecutando $ScriptPath dentro de Docker..." -ForegroundColor Yellow
    docker run --rm -v "${Root}:/project" -w /project $androidImage bash -lc "chmod +x $ScriptPath && ./$ScriptPath"
}

Write-Host "=== 1. Install + Test API ===" -ForegroundColor Cyan
Push-Location server
npm ci --ignore-scripts 2>$null; if (-not $?) { npm install }
npm run test:ci
Pop-Location

Write-Host "=== 2. Test Android + JaCoCo (Docker) ===" -ForegroundColor Cyan
docker pull $androidImage 2>$null | Out-Null
docker run --rm -v "${Root}:/project" -w /project -e GRADLE_USER_HOME=/project/.gradle-preflight $androidImage `
  bash -lc "chmod +x gradlew && ./gradlew :app:testDebugUnitTest :app:jacocoTestReport --no-daemon -Dorg.gradle.jvmargs='-Xmx2560m'"

if ($env:SONAR_TOKEN) {
    Write-Host "=== 3. Sonar (local :9000) ===" -ForegroundColor Cyan
    docker run --rm --add-host=host.docker.internal:host-gateway -v "${Root}:/project" -w /project `
      -e SONAR_TOKEN -e SONAR_HOST_URL="http://host.docker.internal:9000" $androidImage `
      bash -lc "chmod +x gradlew && ./gradlew sonar -Dproject.settings=sonar-project.local.properties -Dsonar.host.url=`$SONAR_HOST_URL -Dsonar.token=`$SONAR_TOKEN --no-daemon"
} else {
    Write-Host "=== 3. Sonar omitido (export SONAR_TOKEN) ===" -ForegroundColor Yellow
}

Write-Host "=== 4. Build production ===" -ForegroundColor Cyan
Invoke-BashScript "ci/build-production.sh"

Write-Host "=== 5. Deploy (local) ===" -ForegroundColor Cyan
$env:HAPPYJUMP_DEPLOY_BASE = "$Root\deploy-runtime"
Invoke-BashScript "ci/deploy-server.sh"

Write-Host ""
Write-Host "Preflight OK" -ForegroundColor Green
Write-Host "  dist: $Root\dist"
Write-Host "  deploy: $env:HAPPYJUMP_DEPLOY_BASE"
Write-Host "  Jenkins UI: http://localhost:8081"
Write-Host "  SonarQube:  http://localhost:9000"
