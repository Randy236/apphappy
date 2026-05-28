# Docker y CI en disco E: — Happy Jump

## Requisitos

- Docker Desktop instalado y **en ejecución**
- **Disk image location** en `E:\DockerDesktop` (Settings → Resources → Advanced)
- Al menos **40 GB libres** en E:
- RAM para Docker: **8 GB** recomendado

## Un solo comando (recomendado)

Desde la raíz del repo (código completo, p. ej. `D:\apphappy-full`):

```powershell
.\scripts\levantar-ci-e.ps1
```

Hace: carpetas en `E:\happyjump-ci`, prueba `hello-world`, `docker compose up -d --build`.

## URLs

| Servicio | URL |
|----------|-----|
| Jenkins | http://localhost:8081 |
| SonarQube | http://localhost:9000 |

## Si algo falla

```powershell
.\scripts\verificar-docker.ps1
.\scripts\fix-jenkins-plugins.ps1
```

## Nota sobre `D:\apphappy`

Si solo tienes `infra/` y `scripts/` en `D:\apphappy`, clona el repo completo:

```powershell
git clone https://github.com/Randy236/apphappy.git D:\apphappy-full
cd D:\apphappy-full
.\scripts\levantar-ci-e.ps1
```
