# Ítem 2 — Código en GitHub + Pruebas integrales (Postman)

## ¿Qué pide el profesor?

Probar la **API completa** (varios endpoints encadenados: login → consultas → crear datos), no solo una función aislada. Herramienta: **Postman**.

**Diferencia con ítem 1:**

| Ítem 1 — Unitarias | Ítem 2 — Integrales |
|--------------------|---------------------|
| Una función / módulo | Varios componentes juntos (API + MySQL + JWT) |
| `npm test`, JUnit | **Postman** Collection Runner |
| Sin red real a veces | HTTP real a `localhost:3000` |

---

## Archivos en el repo

| Archivo | Uso |
|---------|-----|
| `docs/postman/HappyJump-API.postman_collection.json` | Colección con tests automáticos |
| `docs/postman/HappyJump-local.postman_environment.json` | Variable `baseUrl` |
| `docs/CASOS_DE_PRUEBA.csv` | CP-005, CP-006, CP-007… tipo **Integracion** |

---

## Paso a paso (lo que haces tú)

### 1. Preparar entorno

```powershell
# MySQL encendido
cd D:\apphappy-full\server
npm start
```

Debe aparecer: `Happy Jump API http://0.0.0.0:3000`

### 2. Instalar Postman

- Descarga: https://www.postman.com/downloads/
- Cuenta gratuita

### 3. Importar colección

1. Postman → **Import**
2. Arrastra:
   - `docs/postman/HappyJump-API.postman_collection.json`
   - `docs/postman/HappyJump-local.postman_environment.json`
3. Arriba a la derecha selecciona entorno **Happy Jump — Local**

### 4. Probar una request suelta

1. Carpeta **01 - Sistema** → **CP-006 GET /health**
2. **Send**
3. Debe ser **200** y tests en verde (pestaña **Test Results**)

### 5. Ejecutar toda la colección (evidencia principal)

1. Clic derecho en la colección **Happy Jump API — Pruebas integrales**
2. **Run collection** (Collection Runner)
3. Deja el orden por carpetas (01 → 05)
4. **Run Happy Jump API…**
5. Captura pantalla: **todos los tests en verde** (X passed, 0 failed)

### 6. Subir a GitHub

```powershell
cd D:\apphappy-full
git add docs/postman docs/GUIA_ITEM2_POSTMAN_INTEGRACION.md
git commit -m "test: coleccion Postman pruebas integrales"
git push origin main
```

Captura: carpeta `docs/postman/` visible en GitHub.

---

## Qué mostrar al profesor

| # | Evidencia |
|---|-----------|
| 1 | Enlace repo: https://github.com/Randy236/apphappy |
| 2 | Captura carpeta `docs/postman/` en GitHub |
| 3 | Captura Postman con colección importada |
| 4 | Captura **Collection Runner** — todos passed |
| 5 | Captura un request con **Tests** en verde (ej. login Admin) |
| 6 | Tabla CP-005, CP-006, CP-007… de `CASOS_DE_PRUEBA.csv` (tipo Integracion) |

Guarda en: `docs/evidencias-item2/`

---

## Texto para el informe (copiar)

> **Ítem 2 — Pruebas integrales con Postman**  
> Se diseñó la colección *Happy Jump API — Pruebas integrales* que valida la integración entre cliente HTTP, API REST Node.js/Express, autenticación JWT y base MySQL.  
> Flujos probados: health check, login (admin/trabajador), listado y creación de reservas de cancha, listado de salones y reportes administrativos.  
> Cada request incluye assertions automáticas (código HTTP y estructura JSON).  
> Repositorio: `docs/postman/HappyJump-API.postman_collection.json`.  
> Casos alineados: CP-006, CP-007, CP-008, CP-010, CP-012 en `CASOS_DE_PRUEBA.csv`.

---

## Solución de problemas

| Error | Solución |
|-------|----------|
| `ECONNREFUSED` | `npm start` en `server/` |
| Login 401 | Usuarios seed: Admin / Rosisela PIN 1234 |
| Login 409 sesión activa | POST logout o `UPDATE usuarios SET active_token=NULL` |
| Crear cancha 409 | Cambiar fecha/hora en el body o cancelar reserva |
| Tests rojos en Runner | Ejecutar carpetas en orden; login antes de endpoints con Bearer |

---

## Checklist ítem 2

```
[ ] API + MySQL corriendo
[ ] Colección importada en Postman
[ ] Collection Runner: 0 failed
[ ] Capturas en docs/evidencias-item2/
[ ] Push a GitHub con docs/postman/
```

Cuando termines, di **"listo el 2"** y pasamos al **ítem 3** (Sonar cobertura ≥80%).
