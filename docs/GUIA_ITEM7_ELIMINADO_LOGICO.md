# Ítem 7 — Eliminado lógico en todas las entidades

## Qué pide el profesor

No usar `DELETE FROM` en tablas de negocio: marcar registros con **`deleted_at`** y ocultarlos en consultas normales.

## Entidades cubiertas

| Tabla | Columna | Cómo se elimina |
|-------|---------|----------------|
| `usuarios` | `deleted_at` | `DELETE /usuarios/:id` (admin) |
| `reservas_cancha` | `deleted_at` | `DELETE /reservas-cancha/:id` (admin) |
| `reservas_salones` | `deleted_at` | `DELETE /reservas-salones/:id` (admin) |

**Cancelar** reserva sigue siendo distinto: cambia `estado` / `cancelada` (sigue visible en reportes de cancelación). **Eliminar** oculta el registro de listados (`deleted_at`).

---

## PASO 1 — Migración en MySQL

```powershell
cd D:\apphappy-full\server
npm run migrate:009
```

---

## PASO 2 — Verificar

```powershell
npm run soft-delete:verify
```

Debe mostrar **3/3 entidades** y sin `DELETE FROM` en la API.

Swagger sigue al 100 %:

```powershell
npm run swagger:verify
```

(19 operaciones con los 3 DELETE nuevos.)

---

## PASO 3 — Probar en Swagger

1. `POST /auth/login` → Admin / 1234  
2. `DELETE /reservas-cancha/{id}` → respuesta `{ "ok": true, "eliminadoLogico": true }`  
3. `GET /reservas-cancha` → ya no aparece esa fila  

---

## Texto corto para Word

> Se implementó **eliminado lógico** con columna `deleted_at` en **usuarios**, **reservas_cancha** y **reservas_salones**. La API no usa `DELETE FROM`; los endpoints `DELETE` hacen `UPDATE deleted_at = NOW()`. Las consultas filtran `deleted_at IS NULL`. La cancelación de reservas se mantiene como estado de negocio aparte del borrado lógico.

---

## Evidencias

Carpeta `docs/evidencias-item7/`:

1. Salida de `npm run migrate:009`  
2. Salida de `npm run soft-delete:verify`  
3. Captura Swagger de un `DELETE` con respuesta 200  

Cuando termines: **「listo el 7」** → ítem **8 (SonarCloud / Snyk)**.
