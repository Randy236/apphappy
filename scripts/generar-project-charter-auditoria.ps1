# Generar Project Charter de Auditoría SDLC — Happy Jump
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Reports = Join-Path $Root "scripts\auditoria_reports"
$Ent = Join-Path $Root "entregableunidad\entregable-auditoria"

Write-Host "Generando Project Charter..." -ForegroundColor Cyan
Push-Location $Reports
python -m pip install -q -r requirements.txt
python run_project_charter.py
Pop-Location

Copy-Item (Join-Path $Ent "PROJECT_CHARTER_AUDITORIA_SDLC_HAPPY_JUMP.docx") $Ent -Force -ErrorAction SilentlyContinue
Copy-Item (Join-Path $Root "docs\SCRUM_CMMI_HAPPY_JUMP.md") $Ent -Force -ErrorAction SilentlyContinue

Write-Host "`nListo: entregableunidad\entregable-auditoria\" -ForegroundColor Green
Get-ChildItem $Ent | Format-Table Name, Length -AutoSize
