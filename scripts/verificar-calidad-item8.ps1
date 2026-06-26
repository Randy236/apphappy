# Checklist ítem 8 — SonarCloud + Snyk (config + tests locales)
# Uso: .\scripts\verificar-calidad-item8.ps1

$ErrorActionPreference = "Continue"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

Write-Host "`n=== Ítem 8 — Verificación calidad ===`n" -ForegroundColor Cyan

$script:allOk = $true

function Test-FileOk($path, $label) {
    if (Test-Path $path) {
        Write-Host "OK  $label" -ForegroundColor Green
        return $true
    }
    Write-Host "FALTA  $label ($path)" -ForegroundColor Red
    $script:allOk = $false
    return $false
}

Test-FileOk "sonar-project.properties" "sonar-project.properties"
Test-FileOk ".github\workflows\sonarcloud.yml" "Workflow SonarCloud"
Test-FileOk ".github\workflows\snyk.yml" "Workflow Snyk"
Test-FileOk ".github\workflows\api-tests.yml" "Workflow API Tests"
Test-FileOk "scripts\publicar-sonarcloud.ps1" "Script publicar Sonar"

if (-not $env:SONAR_TOKEN) {
    Write-Host "AVISO  SONAR_TOKEN no definido en esta ventana (normal; debe estar en GitHub Secrets)" -ForegroundColor Yellow
} else {
    Write-Host "OK  SONAR_TOKEN presente localmente" -ForegroundColor Green
}

Write-Host "`n--- Tests API (server) ---" -ForegroundColor Cyan
Push-Location server
npm test 2>&1 | Out-Host
if ($LASTEXITCODE -ne 0) { $script:allOk = $false }
Pop-Location

$jacocoXml = "app\build\reports\jacoco\jacocoTestReport\jacocoTestReport.xml"
if (Test-Path $jacocoXml) {
    $bytes = (Get-Item $jacocoXml).Length
    Write-Host "OK  JaCoCo XML existe ($bytes bytes) — listo para Sonar" -ForegroundColor Green
} else {
    Write-Host "AVISO  Sin JaCoCo XML. Ejecuta: .\scripts\run-coverage-local.ps1" -ForegroundColor Yellow
}

Write-Host "`n--- Pasos manuales (informe) ---" -ForegroundColor Cyan
Write-Host "  1) SonarCloud: Automatic Analysis OFF"
Write-Host "  2) GitHub Secrets: SONAR_TOKEN, SNYK_TOKEN"
Write-Host "  3) .\scripts\publicar-sonarcloud.ps1  o  Actions -> SonarCloud"
Write-Host "  4) Capturas: docs/evidencias-item8/"
Write-Host "  5) Dashboard: https://sonarcloud.io/project/overview?id=Randy236_apphappy`n"

if (-not $script:allOk) { exit 1 }
Write-Host "Verificación local OK (revisa AVISOS arriba).`n" -ForegroundColor Green
