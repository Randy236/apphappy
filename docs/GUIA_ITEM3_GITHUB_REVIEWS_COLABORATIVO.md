# Ítem 3 — Colaboración en GitHub (mínimo 2 reviews de aprobación)

## Qué pide el profesor

Que se **vea en GitHub** que el equipo trabajó **en colaboración**, con al menos **2 revisiones aprobadas** (Pull Request reviews con estado **Approved**).

**No basta** con commits solos: hace falta **Pull Requests + Reviews**.

---

## Qué necesitas

| Requisito | Detalle |
|-----------|---------|
| Repositorio | https://github.com/Randy236/apphappy |
| Rol **Admin** en el repo | Para invitar colaboradores y (opcional) proteger `main` |
| **2 personas** que aprueben | Compañeros de equipo con cuenta GitHub (o 2 compañeros distintos en 1–2 PRs) |
| Git instalado | En tu PC |

---

## Resumen del flujo (lo que verá el profesor)

```
rama feature → push → Pull Request → Reviewer 1 ✅ Approve
                                  → Reviewer 2 ✅ Approve
                                  → Merge
```

Mínimo **2 eventos "Approved"** en total (en uno o dos PRs).

---

## PASO 1 — Invitar colaboradores (5 min)

1. Abre: https://github.com/Randy236/apphappy  
2. **Settings** → **Collaborators** (o **Manage access**)  
3. **Add people** → usuario GitHub de cada compañero → rol **Write**  
4. Ellos aceptan la invitación por correo.

*Si trabajas solo:* pide a **2 compañeros del salón** que solo entren a **aprobar** el PR (no tienen que programar).

---

## PASO 2 — Proteger la rama `main` (opcional, recomendado) (10 min)

1. **Settings** → **Branches** → **Add branch protection rule**  
2. Branch name pattern: `main`  
3. Marca:
   - ✅ **Require a pull request before merging**
   - ✅ **Require approvals** → número: **2** (si el curso pide 2 en cada merge)
   - O **1** si solo piden 2 approvals en todo el proyecto (pregunta al profesor)
4. ✅ **Require status checks** (opcional): selecciona **API Tests** si ya corre en Actions  
5. **Save changes**

Si no tienes permisos de admin, el profesor puede activarlo; igual puedes hacer PRs y pedir **Approve** manual.

---

## PASO 3 — Primer Pull Request con reviews (20 min)

### 3.1 Crear rama y cambio pequeño

En PowerShell:

```powershell
cd D:\apphappy-full
git checkout main
git pull origin main
git checkout -b feature/item3-colaboracion-pr1
```

Haz un cambio mínimo (ejemplo: archivo de evidencia):

```powershell
New-Item -ItemType Directory -Force -Path docs\evidencias-github | Out-Null
echo "PR1 colaboracion equipo - $(Get-Date -Format yyyy-MM-dd)" | Out-File docs\evidencias-github\PR1-log.txt -Encoding utf8
git add docs/evidencias-github/PR1-log.txt
git commit -m "docs: evidencia colaboracion PR1 (item 3)"
git push -u origin feature/item3-colaboracion-pr1
```

### 3.2 Abrir Pull Request

1. GitHub → repo → banner **Compare & pull request**  
2. **base:** `main` ← **compare:** `feature/item3-colaboracion-pr1`  
3. Título: `docs: evidencia colaboración — PR1`  
4. Descripción (usa la plantilla si aparece)  
5. **Reviewers** (panel derecho): asigna **2 compañeros**  
6. **Create pull request**

### 3.3 Que aprueben (tus compañeros)

Cada revisor:

1. Abre el PR → pestaña **Files changed**  
2. **Review changes** → **Approve** → **Submit review**  
3. (Opcional) comentario: *"LGTM, documentación OK"*

### 3.4 Merge

Cuando haya **2 approvals** (o las que pida el profesor):

1. **Merge pull request** → **Confirm merge**  
2. En tu PC:

```powershell
git checkout main
git pull origin main
```

---

## PASO 4 — Segundo PR (si el profesor pide 2 PRs distintos) (15 min)

Repite el proceso con otra rama:

```powershell
git checkout main
git pull origin main
git checkout -b feature/item3-colaboracion-pr2
echo "PR2 - segunda evidencia colaboracion" | Out-File docs\evidencias-github\PR2-log.txt -Encoding utf8
git add docs/evidencias-github/PR2-log.txt
git commit -m "docs: evidencia colaboracion PR2 (item 3)"
git push -u origin feature/item3-colaboracion-pr2
```

Abre **PR #2**, 2 reviewers, 2 approvals, merge.

---

## PASO 5 — Enlazar con Jira (opcional, suma puntos)

En el comentario del PR escribe:

```
Relacionado: HJ-XX
```

O en el commit:

```
HJ-XX docs: evidencia colaboracion PR1
```

---

## PASO 6 — Capturas para el profesor (10 min)

Guarda en `docs/evidencias-github/`:

| # | Qué capturar | Archivo |
|---|--------------|---------|
| 1 | Lista de **Pull requests** (2 PRs merged o abiertos) | `01-lista-prs.png` |
| 2 | PR con **2 approvals** visibles (Checks / Reviews) | `02-pr-dos-approvals.png` |
| 3 | Pestaña **Files changed** con comentarios | `03-files-changed.png` |
| 4 | **Insights** → Contributors o **Commits** varios autores | `04-contributors.png` |
| 5 | Settings → Collaborators (nombres del equipo) | `05-collaborators.png` |

---

## PASO 7 — Texto para el informe (copiar)

> El desarrollo se realizó de forma colaborativa en GitHub (repositorio apphappy). Se utilizaron ramas feature y Pull Requests con revisión por pares. Se registraron al menos dos aprobaciones (Approved) en los PRs [indicar números, ej. #12 y #13], cumpliendo el criterio de trabajo en equipo y control de calidad del código antes de integrar a `main`. Los workflows de GitHub Actions (API Tests) validan los cambios en cada PR.

---

## Checklist ítem 3

```
[ ] 2+ colaboradores invitados al repo
[ ] Al menos 1 PR abierto desde rama feature (ideal: 2 PRs)
[ ] 2 reviews con estado Approved (total, visibles en GitHub)
[ ] Merge a main
[ ] Capturas en docs/evidencias-github/
[ ] (Opcional) Branch protection con require PR
```

---

## Problemas frecuentes

| Problema | Solución |
|----------|----------|
| No veo "Approve" | El revisor debe usar **Review changes**, no solo comentar |
| Solo 1 approval | Asigna **2 reviewers** distintos |
| No puedo push | `git pull` primero; verifica que te invitaron con Write |
| Actions en rojo | Arregla tests o merge igual si el profesor no exige verde en PR |

---

## Siguiente ítem

**Ítem 4 — Swagger 100 %** → `GUIA_ITEM4_SWAGGER_100.md` (se creará al decir "listo el 3").

Cuando tengas las capturas, di **"listo el 3"**.
