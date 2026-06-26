# Genera JAR + informes Word/Excel desde SonarCloud API
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
Set-Location $Root

Write-Host "Compilando sonar-informe.jar..." -ForegroundColor Cyan
.\gradlew.bat :tools:sonar-informe:fatJar --no-daemon -q
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$jar = Get-ChildItem "tools\sonar-informe\build\libs\sonar-informe-all.jar" | Select-Object -First 1
if (-not $jar) {
    Write-Host "No se encontro el JAR en tools\sonar-informe\build\libs\" -ForegroundColor Red
    exit 1
}

$outDir = Join-Path $Root "entregableunidad\entregable-04"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
Copy-Item $jar.FullName (Join-Path $outDir "sonar-informe.jar") -Force

if (-not $env:SONAR_TOKEN -or $env:SONAR_TOKEN.Trim().Length -lt 20) {
    Write-Host @"

JAR listo: entregableunidad\entregable-04\sonar-informe.jar

Para generar Word + Excel:
  `$env:SONAR_TOKEN = "TU_TOKEN"
  java -jar entregableunidad\entregable-04\sonar-informe.jar --out entregableunidad\entregable-04

"@ -ForegroundColor Green
    exit 0
}

Write-Host "Generando informes Word y Excel..." -ForegroundColor Cyan
java -jar (Join-Path $outDir "sonar-informe.jar") --out $outDir
exit $LASTEXITCODE
