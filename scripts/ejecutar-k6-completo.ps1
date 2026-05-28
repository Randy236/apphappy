# Levanta API demo + ejecuta las 3 pruebas k6 y guarda evidencia en k6/results/
# Uso: .\scripts\ejecutar-k6-completo.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Results = Join-Path $Root "k6\results"
New-Item -ItemType Directory -Force -Path $Results | Out-Null

$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" +
            [System.Environment]::GetEnvironmentVariable("Path", "User")

if (-not (Get-Command k6 -ErrorAction SilentlyContinue)) {
    Write-Host "Instalando k6 con winget..." -ForegroundColor Yellow
    winget install GrafanaLabs.k6 --accept-package-agreements --accept-source-agreements
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" +
                [System.Environment]::GetEnvironmentVariable("Path", "User")
}

Set-Location $Root

# API en segundo plano
$apiJob = Start-Job -ScriptBlock {
    Set-Location $using:Root
    Set-Location server
    node scripts/k6-api.mjs 2>&1
}

Start-Sleep -Seconds 2
try {
    $ping = Invoke-WebRequest -Uri "http://localhost:3000/hello" -UseBasicParsing -TimeoutSec 5
    if ($ping.Content -notmatch "Good Morning") { throw "API no responde bien" }
} catch {
    Stop-Job $apiJob -ErrorAction SilentlyContinue
    Receive-Job $apiJob
    throw "No se pudo levantar la API k6 en puerto 3000. $($_.Exception.Message)"
}

Write-Host "API OK en http://localhost:3000" -ForegroundColor Green

$tests = @("load-test.js", "smoke-test.js", "stress-test.js")
foreach ($t in $tests) {
    $out = Join-Path $Results ($t -replace '\.js$', '.txt')
    Write-Host "`n=== k6 run $t ===" -ForegroundColor Cyan
    k6 run "k6/$t" 2>&1 | Tee-Object -FilePath $out
    if ($LASTEXITCODE -ne 0) {
        Stop-Job $apiJob -ErrorAction SilentlyContinue
        Remove-Job $apiJob -Force -ErrorAction SilentlyContinue
        exit $LASTEXITCODE
    }
}

Stop-Job $apiJob -ErrorAction SilentlyContinue
Remove-Job $apiJob -Force -ErrorAction SilentlyContinue

Write-Host "`nListo. Evidencias en: $Results" -ForegroundColor Green
