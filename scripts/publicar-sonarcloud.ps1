# Sube análisis + cobertura JaCoCo a SonarCloud (desde tu PC, sin esperar GitHub Actions).
# Uso:
#   $env:SONAR_TOKEN = "pega_aqui_el_token_de_sonarcloud"
#   .\scripts\publicar-sonarcloud.ps1
#
# Token: https://sonarcloud.io → avatar → My Account → Security → Generate Token

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

if (-not $env:SONAR_TOKEN) {
    Write-Host "Falta SONAR_TOKEN. Ver instrucciones abajo." -ForegroundColor Yellow
    $missingToken = $true
} else {
    $env:SONAR_TOKEN = $env:SONAR_TOKEN.Trim().Trim('"').Trim("'")
    $missingToken = $env:SONAR_TOKEN.Length -lt 20
}

if ($missingToken) {
    Write-Host @"

Falta SONAR_TOKEN valido en esta ventana de PowerShell.

1. https://sonarcloud.io → avatar → My Account → Security → Generate Token
2. Copia el token completo (sin espacios)
3. En PowerShell:

   `$env:SONAR_TOKEN = "TU_TOKEN"
   .\scripts\publicar-sonarcloud.ps1

Automatic Analysis debe estar OFF:
   https://sonarcloud.io/project/analysis_method?id=Randy236_apphappy

"@ -ForegroundColor Yellow
    exit 1
}

if (-not (Test-Path "local.properties")) {
    Write-Host "Falta local.properties con sdk.dir" -ForegroundColor Red
    exit 1
}

Write-Host "Comprobando token con SonarCloud..." -ForegroundColor Cyan
try {
    $headers = @{ Authorization = "Bearer $($env:SONAR_TOKEN)" }
    $validate = Invoke-RestMethod -Uri "https://sonarcloud.io/api/authentication/validate" -Headers $headers -Method Get
    if (-not $validate.valid) {
        throw "Token rechazado por SonarCloud"
    }
    Write-Host "Token OK ($($env:SONAR_TOKEN.Substring(0, [Math]::Min(4, $env:SONAR_TOKEN.Length)))...)" -ForegroundColor Green
} catch {
    Write-Host @"

ERROR: El token NO es valido para SonarCloud.

Genera uno NUEVO:
  1) Misma cuenta GitHub que el repo (Randy236)
  2) https://sonarcloud.io → avatar → My Account → Security → Generate Token
  3) Copia SIN comillas extra ni espacios
  4) `$env:SONAR_TOKEN = "TOKEN_NUEVO"
     .\scripts\publicar-sonarcloud.ps1

Detalle tecnico: $($_.Exception.Message)

"@ -ForegroundColor Red
    exit 1
}

Write-Host "Deteniendo daemons Gradle..." -ForegroundColor Cyan
.\gradlew.bat --stop 2>$null

$jvm = "-Xmx2560m -XX:MaxMetaspaceSize=512m -Xss1m -Dfile.encoding=UTF-8"
$logFile = Join-Path $Root "sonar-log-local.txt"

Write-Host "Tests + JaCoCo + SonarCloud (~4-8 min)..." -ForegroundColor Cyan
Write-Host "Log: $logFile" -ForegroundColor DarkGray

$gradleCmd = ".\gradlew.bat sonar --no-daemon --stacktrace `"-Dorg.gradle.jvmargs=$jvm`" `"-Dsonar.token=$($env:SONAR_TOKEN)`""
cmd /c "$gradleCmd > `"$logFile`" 2>&1"
$gradleExit = $LASTEXITCODE

Get-Content $logFile -Tail 8 | ForEach-Object { Write-Host $_ }

if ($gradleExit -ne 0) {
    Write-Host "`n=== ERROR (ultimas lineas utiles) ===" -ForegroundColor Red
    $lines = Get-Content $logFile -ErrorAction SilentlyContinue
    $lines | Select-String -Pattern 'What went wrong|Caused by|Automatic Analysis|Unauthorized|not found|ERROR|Exception' |
        Select-Object -Last 15 | ForEach-Object { Write-Host $_.Line -ForegroundColor Red }

    $logText = ($lines -join "`n")
    if ($logText -match 'Automatic Analysis is enabled') {
        Write-Host "`nDesactiva Automatic Analysis y espera 1 min.`n" -ForegroundColor Yellow
    } elseif ($logText -match 'Unauthorized|401|Not authorized|not found') {
        Write-Host "`nToken sin permiso en Randy236_apphappy. Usa cuenta Randy236 en SonarCloud.`n" -ForegroundColor Yellow
    }
    exit $gradleExit
}

Write-Host @"

Listo. Abre SonarCloud (espera 1-2 min y refresca F5):

  https://sonarcloud.io/project/overview?id=Randy236_apphappy

En Overview debe aparecer Coverage >= 80%.

Captura esa pantalla para el entregable 3.

"@ -ForegroundColor Green
