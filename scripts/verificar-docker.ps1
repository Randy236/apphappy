# Comprueba que Docker no sera cuello de botella. Exit 0 = OK.
# Uso: .\scripts\verificar-docker.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\lib-docker.ps1"

Write-Host "=== Discos ===" -ForegroundColor Cyan
foreach ($l in @("C","D","E")) {
    $d = Get-PSDrive $l -EA SilentlyContinue
    if ($d) { Write-Host ("  {0}: {1:N1} GB libres" -f $l, ($d.Free/1GB)) }
}

Write-Host "`n=== Docker ===" -ForegroundColor Cyan
$exe = Get-DockerExe
Write-Host "  Ejecutable: $exe"
Invoke-Docker version --format "  Server: {{.Server.Version}}"
Invoke-Docker ps --format "  Contenedor: {{.Names}} ({{.Status}})" 2>$null

Write-Host "`n=== Prueba hello-world ===" -ForegroundColor Cyan
Invoke-Docker run --rm hello-world 2>&1 | Select-String "Hello from Docker" -Quiet
if ($?) { Write-Host "  OK" -ForegroundColor Green } else { throw "hello-world fallo" }

Write-Host "`n=== Rutas E: ===" -ForegroundColor Cyan
@("E:\happyjump-ci", "E:\DockerDesktop") | ForEach-Object {
    Write-Host ("  {0}: {1}" -f $_, (Test-Path $_))
}

Write-Host "`nDocker listo." -ForegroundColor Green
