# Ítem 1 — Documento de avance (paso a paso)

Objetivo: entregar un **Word o PDF** que diga al profesor *“esto es la continuación del entregable 1 de la Unidad 1”* y muestre **qué avanzaron** en Unidad 2.

**Plantilla base:** `docs/DOCUMENTO_AVANCE_UNIDAD2.md`

---

## PASO 1 — Abrir la plantilla (2 min)

1. Abre el Explorador de archivos.
2. Ve a: `D:\apphappy-full\docs\`
3. Abre `DOCUMENTO_AVANCE_UNIDAD2.md` con:
   - **Bloc de notas**, o
   - **Word** (arrastra el archivo), o
   - **VS Code / Cursor**

---

## PASO 2 — Portada y datos del equipo (5 min)

Completa al inicio del documento:

| Campo | Qué escribir (ejemplo) |
|-------|------------------------|
| **Fecha** | 30/05/2026 |
| **Equipo** | Nombres de integrantes |
| **Versión** | 2.0 |

En **Historial de revisiones**, fila 2.0:

| Versión | Fecha | Autor | Cambio |
|---------|--------|--------|--------|
| 2.0 | 30/05/2026 | Tu nombre + equipo | Documento de avance Unidad 2 |

---

## PASO 3 — Porcentajes de avance (5 min)

En **sección 1 — Resumen ejecutivo**, sustituye `___` por números realistas:

| Campo | Valor sugerido | Por qué |
|-------|----------------|---------|
| Avance global del producto | **75 %** o **80 %** | App + API funcionan; faltan cobertura 100 % y algunos entregables |
| Avance documentación y procesos | **70 %** | Plan de pruebas, Postman, guías listas; Jira/Sonar en curso |

En **sección 4 — TS, VER, IP**:

| Área | % sugerido | Pendiente (ya está en la tabla) |
|------|------------|----------------------------------|
| **TS** | 80 % | Swagger 100 %, soft delete |
| **VER** | 65 % | Cobertura 100 %, k6 completo |
| **IP** | 70 % | 2 reviews GitHub, Sonar verde |

*Ajusta si tu profesor pide otros números; no pongas 100 % si aún no está todo.*

---

## PASO 4 — Revisar tablas (no borrar, solo verificar) (10 min)

Las secciones **2, 3, 5, 6, 7** ya tienen texto. **No tienes que reescribir todo.**

Solo confirma que sea verdad en tu proyecto:

- [ ] ¿Tienes repo en GitHub? → https://github.com/Randy236/apphappy  
- [ ] ¿Existe `docs/REQUERIMIENTOS.csv`? → sí  
- [ ] ¿La app tiene login, cancha, salones? → sí  
- [ ] ¿Postman en `docs/postman/`? → sí  

Si algo no lo tienes, cambia “100 %” por “En progreso” en esa fila.

---

## PASO 5 — Sacar capturas de pantalla (15 min)

Crea la carpeta:

```
D:\apphappy-full\docs\evidencias-avance\
```

Guarda **mínimo 5** capturas:

| # | Qué capturar | Nombre archivo |
|---|--------------|----------------|
| 1 | Página principal del repo en GitHub | `01-github-repo.png` |
| 2 | App Android: pantalla Login o Cancha (emulador) | `02-app-pantalla.png` |
| 3 | Navegador: http://localhost:3000/swagger-ui/ (con API corriendo) | `03-swagger.png` |
| 4 | Postman: Collection Runner con tests en verde | `04-postman.png` |
| 5 | GitHub → Actions → workflow **API Tests** verde | `05-actions.png` |

**Opcional:** carpeta `docs/` abierta mostrando REQUERIMIENTOS.csv y PLAN_MAESTRO.

### Cómo levantar API para captura 3

```powershell
cd D:\apphappy-full\server
npm start
```

Abre: http://localhost:3000/swagger-ui/

---

## PASO 6 — Pasar a Word (10 min)

1. Abre **Microsoft Word** → documento en blanco.
2. Copia **todo** el contenido de `DOCUMENTO_AVANCE_UNIDAD2.md` (con tus `___` ya rellenados).
3. Pega en Word.
4. Aplica formato rápido:
   - Título 1: “Documento de avance — Happy Jump”
   - Subtítulos: secciones 1, 2, 3…
   - Tablas: seleccionar → Insertar → Tabla (Word suele detectar tablas al pegar desde Markdown; si no, deja texto y formatea manual).

5. **Insertar capturas** después de las secciones 5, 6 o 7:
   - Insertar → Imágenes → desde `docs\evidencias-avance\`
   - Pie de foto: “Figura 1. Repositorio GitHub”, etc.

6. **Carátula** (primera página):

```
UNIVERSIDAD / INSTITUTO: _______________
MATERIA: _______________
UNIDAD 2 — DOCUMENTO DE AVANCE

Proyecto: Happy Jump
Continuación del Entregable 1 — Unidad 1

Integrantes: _______________
Fecha: _______________
Repositorio: https://github.com/Randy236/apphappy
```

---

## PASO 7 — Conclusiones (5 min)

La sección **10** ya tiene un párrafo. Puedes dejarlo o personalizar con una frase tuya, por ejemplo:

> Se cumplió la mayor parte del alcance de Unidad 1: sistema operativo con app Android y API REST. Los pendientes de Unidad 2 (cobertura 100 %, k6 completo, reviews en GitHub y SonarCloud) están planificados en el checklist de entregables finales.

---

## PASO 8 — Guardar y entregar (2 min)

1. Word → **Archivo → Guardar como**
2. Nombre: `DOCUMENTO_AVANCE_UNIDAD2.docx`
3. **Archivo → Exportar → PDF** → `DOCUMENTO_AVANCE_UNIDAD2.pdf`
4. Sube el PDF a la plataforma del profesor (o imprime si lo pide).

Opcional: sube también a `docs/evidencias-avance/` en el repo.

---

## Checklist final ítem 1

```
[ ] Fecha y nombres en portada
[ ] Porcentajes TS / VER / IP y avance global
[ ] 5 capturas en docs/evidencias-avance/
[ ] Word con carátula + tablas + imágenes
[ ] PDF exportado
[ ] Frase en carátula: "Continuación Entregable 1 Unidad 1"
```

---

## Texto listo para pegar en el resumen (si quieres copiar)

> En la Unidad 1 definimos requisitos, arquitectura y plan de pruebas para Happy Jump. En este documento de avance (Unidad 2) reportamos la implementación del cliente Android y la API Node.js con MySQL, las pruebas con Postman y pruebas unitarias, la integración con GitHub Actions y la documentación de procesos Scrum/CMMI. El avance global del producto se estima en aproximadamente 75–80 %, con pendientes acordados en cobertura de código, k6, eliminado lógico y evidencias en SonarCloud.

---

Cuando termines, di **"listo el 1"** para el ítem 2 (Jira TS, VER, IP).
