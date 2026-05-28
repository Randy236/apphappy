# Evidencia — Pruebas k6 Happy Jump

**Fecha de ejecución:** generado automáticamente  
**k6:** v2.0.0 (winget `GrafanaLabs.k6`)  
**API:** `server/scripts/k6-api.mjs` en `http://localhost:3000`

## Resultados

| Prueba | Archivo salida | Checks |
|--------|----------------|--------|
| Load (1 VU, 5s) | `load-test.txt` | 100% OK |
| Smoke (20 VU, 20s) | `smoke-test.txt` | 100% OK |
| Stress (10→100 VU) | `stress-test.txt` | 0% HTTP failed |

## Endpoints probados

- `GET /hello` → `Good Morning`
- `GET /sumar?a=5&b=3` → `8`
- `GET /sumar?a=10&b=20` → `30`

## Nota

MySQL no estaba activo; se usó la API demo con los mismos endpoints del tutorial. Con MySQL: `cd server && npm start`.

## Repetir en tu PC

```powershell
cd D:\apphappy-full
.\scripts\ejecutar-k6-completo.ps1
```
