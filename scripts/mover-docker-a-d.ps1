# Mueve %LOCALAPPDATA%\Docker a D:\DockerDesktop y deja un enlace en C:
# Ejecutar con Docker Desktop CERRADO (Quit).
# Uso (PowerShell como usuario normal): .\scripts\mover-docker-a-d.ps1

$ErrorActionPreference = "Stop"
$src = Join-Path $env:LOCALAPPDATA "Docker"
$dstRoot = "D:\DockerDesktop"
$dst = Join-Path $dstRoot "Docker"

if (Get-Process "Docker Desktop" -ErrorAction SilentlyContinue) {
    Write-Host "Cierra Docker Desktop (Quit) y vuelve a ejecutar." -ForegroundColor Red
    exit 1
}

wsl --shutdown 2>$null
Start-Sleep -Seconds 3

New-Item -ItemType Directory -Force -Path $dstRoot | Out-Null

if (Test-Path $src) {
    if (Test-Path $dst) {
        Write-Host "Ya existe $dst - omitiendo copia." -ForegroundColor Yellow
    } else {
        Write-Host "Moviendo $src -> $dst ..." -ForegroundColor Cyan
        Move-Item -Path $src -Destination $dst -Force
    }
    if (-not (Test-Path $src)) {
        cmd /c mklink /J "$src" "$dst" | Out-Null
        Write-Host "Junction creado: $src -> $dst" -ForegroundColor Green
    }
} else {
    Write-Host "No existe $src; creando junction nuevo." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path $dst | Out-Null
    cmd /c mklink /J "$src" "$dst" | Out-Null
}

Write-Host ""
Write-Host "Listo. Abre Docker Desktop y espera a que arranque." -ForegroundColor Green
Write-Host "Luego: cd D:\apphappy\infra\docker; docker compose up -d"
