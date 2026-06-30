# Happy Jump — Documentación del proyecto

Bienvenido al sitio de documentación del sistema **Happy Jump**: aplicación Android para gestión de reservas de **cancha deportiva** y **salones de eventos**, con API **Node.js + Express** y base de datos **MySQL**.

Este sitio se genera con **[MkDocs Material](https://squidfunk.github.io/mkdocs-material/)** a partir de los archivos en la carpeta `docs/` del repositorio [Randy236/apphappy](https://github.com/Randy236/apphappy).

---

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Cliente móvil | Kotlin + Jetpack Compose |
| API REST | Node.js 18+ + Express + JWT |
| Base de datos | MySQL 8+ |
| Calidad | SonarCloud, Snyk, JaCoCo, GitHub Actions |
| CI/CD opcional | Jenkins + Docker |

---

## Módulos del sistema

- **Login** por nombre y PIN (roles trabajador / administrador)
- **Cancha** — reservas por horario, adelantos, cancelaciones
- **Salones** — reservas de eventos con validación de traslapes
- **Reportes** — ingresos por período (administrador)
- **Perfil** — cambio de PIN y cierre de sesión
- **Sesión única** — un dispositivo activo por usuario

---

## Documentos clave para el curso

| Documento | Ubicación en el repo |
|-----------|----------------------|
| Requisitos funcionales | `docs/REQUERIMIENTOS.csv` |
| Casos de prueba | `docs/CASOS_DE_PRUEBA.csv` |
| Matriz actividades–requisitos | `docs/MATRIZ_ACTIVIDADES_REQUERIMIENTOS.csv` |
| Plan maestro de pruebas | [Plan maestro](PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md) |
| Checklist entrega profesor | [Checklist](CHECKLIST_ENTREGA_PROFESOR.md) |
| Evidencias Sonar / Snyk | [Calidad](EVIDENCIAS_CALIDAD_SONAR_SNYK.md) |
| Project Charter auditoría SDLC | [Charter](PROJECT_CHARTER_AUDITORIA_SDLC_HAPPY_JUMP.md) |

Los archivos **CSV** se descargan desde el repositorio en GitHub (no se renderizan como página web en MkDocs).

---

## Inicio rápido (desarrolladores)

### Backend

```bash
cd server
npm install
npm start
```

API por defecto: `http://0.0.0.0:3000` — health: `GET /health`

### App Android

Configurar `happyJump.api.baseUrl` en `local.properties`:

- Emulador: `http://10.0.2.2:3000/`
- Celular (misma Wi‑Fi): `http://IP_DE_TU_PC:3000/`

```bash
./gradlew :app:installDebug
```

### Docker (opcional)

```bash
docker compose -f docker-compose.app.yml up -d --build
```

---

## Cómo ver esta documentación en local

```bash
pip install -r requirements-mkdocs.txt
mkdocs serve
```

Abre [http://127.0.0.1:8000](http://127.0.0.1:8000).

Más detalle: [Guía MkDocs](GUIA_MKDOCS.md).

---

## Enlaces útiles

- [Repositorio GitHub](https://github.com/Randy236/apphappy)
- [SonarCloud](https://sonarcloud.io/project/overview?id=Randy236_apphappy)
- Swagger (API local): `http://localhost:3000/swagger-ui/`
