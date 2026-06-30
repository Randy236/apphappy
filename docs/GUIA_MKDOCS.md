# Guía MkDocs — Happy Jump

Esta guía explica cómo levantar, editar y publicar la documentación del proyecto con **MkDocs** y el tema **Material**, como suele pedir el curso.

---

## 1. Requisitos

- Python 3.9 o superior
- `pip` (gestor de paquetes Python)

---

## 2. Instalación (una sola vez)

Desde la raíz del repositorio (`apphappy/`):

```bash
pip install -r requirements-mkdocs.txt
```

O con entorno virtual (recomendado):

```bash
python3 -m venv .venv-mkdocs
source .venv-mkdocs/bin/activate   # Windows: .venv-mkdocs\Scripts\activate
pip install -r requirements-mkdocs.txt
```

---

## 3. Ver la documentación en tu PC

```bash
mkdocs serve
```

Abre el navegador en: **http://127.0.0.1:8000**

- Los cambios en archivos `.md` dentro de `docs/` se recargan al guardar.
- La configuración del menú está en `mkdocs.yml` (raíz del repo).

Para detener el servidor: `Ctrl + C`.

---

## 4. Generar el sitio estático (HTML)

```bash
mkdocs build
```

La salida queda en la carpeta `site/` (no subir `site/` a Git; está en `.gitignore` si se agrega).

---

## 5. Publicar en GitHub Pages

### Opción A — Automática (GitHub Actions)

Al hacer push a `main`, el workflow `.github/workflows/mkdocs.yml` publica el sitio en:

**https://randy236.github.io/apphappy/**

Requisito: en el repo → **Settings → Pages → Source: GitHub Actions**.

### Opción B — Manual desde tu PC

```bash
mkdocs gh-deploy
```

(Necesitas permisos de escritura en el repo y `git` configurado.)

---

## 6. Cómo agregar una página nueva

1. Crea o edita un archivo `.md` dentro de `docs/` (ej. `docs/mi-nueva-guia.md`).
2. Regístralo en `mkdocs.yml` bajo `nav:`:

```yaml
nav:
  - Inicio: index.md
  - Mi sección:
      - Mi nueva guía: mi-nueva-guia.md
```

3. Verifica con `mkdocs serve`.

---

## 7. Archivos que NO van en MkDocs

Estos permanecen en `docs/` pero no son páginas web:

| Tipo | Ejemplos | Uso |
|------|----------|-----|
| CSV | `REQUERIMIENTOS.csv`, `CASOS_DE_PRUEBA.csv` | Excel / Sheets |
| JSON | Postman collections | Postman |
| DOCX | Project Charter Word | Entrega formal |

Enlázalos desde `index.md` o desde páginas Markdown si el profesor debe descargarlos.

---

## 8. Evidencia para el profesor

Capturas sugeridas:

1. Terminal con `mkdocs serve` y URL `127.0.0.1:8000`.
2. Navegador mostrando la portada y el menú lateral.
3. GitHub Pages activo (`randy236.github.io/apphappy`) si ya desplegaste.
4. Commit/PR donde agregaste `mkdocs.yml` y `index.md`.

---

## 9. Solución de problemas

| Problema | Solución |
|----------|----------|
| `mkdocs: command not found` | Activa el venv o reinstala: `pip install -r requirements-mkdocs.txt` |
| Página no aparece en el menú | Agrégala en `nav:` de `mkdocs.yml` |
| Error al desplegar Pages | Revisa Settings → Pages → GitHub Actions y el workflow en Actions |
| Tema roto | Verifica `mkdocs-material` en `requirements-mkdocs.txt` |

---

## 10. Referencias

- [MkDocs](https://www.mkdocs.org/)
- [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/)
