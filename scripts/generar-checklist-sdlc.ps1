# Genera Checklist SDLC Audit completo — Happy Jump
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Reports = Join-Path $Root "scripts\auditoria_reports"

Write-Host "Generando Checklist SDLC..." -ForegroundColor Cyan
Push-Location $Reports
python -m pip install -q -r requirements.txt
python run_checklist_sdlc.py
Pop-Location
Write-Host "`nArchivos en entregableunidad\entregable-auditoria\" -ForegroundColor Green
Get-ChildItem (Join-Path $Root "entregableunidad\entregable-auditoria") | Format-Table Name, Length -AutoSize
