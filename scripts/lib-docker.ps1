# Resuelve docker.exe (Docker Desktop en C:, D: o E:)
function Get-DockerExe {
    $candidates = @(
        (Get-Command docker -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source),
        "$env:ProgramFiles\Docker\Docker\resources\bin\docker.exe",
        "${env:ProgramFiles(x86)}\Docker\Docker\resources\bin\docker.exe",
        "E:\DockerDesktop\Docker\resources\bin\docker.exe",
        "D:\DockerDesktop\Docker\resources\bin\docker.exe",
        "E:\Program Files\Docker\Docker\resources\bin\docker.exe"
    ) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
    if (-not $candidates) {
        throw "No se encontro docker.exe. Abre Docker Desktop y verifica que este en el PATH o en E:\DockerDesktop."
    }
    return $candidates
}

function Invoke-Docker {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Args)
    $exe = Get-DockerExe
    $dir = Split-Path $exe -Parent
    if ($env:Path -notlike "*$dir*") { $env:Path = "$dir;$env:Path" }
    & $exe @Args
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
