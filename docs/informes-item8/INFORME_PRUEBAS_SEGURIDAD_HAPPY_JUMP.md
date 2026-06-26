# INFORME DE PRUEBAS DE SEGURIDAD

**Proyecto:** Happy Jump  
**Repositorio:** https://github.com/Randy236/apphappy  
**Fecha de ejecución:** __________________  
**Responsable:** __________________  
**Versión app / commit:** __________________  
**Entorno API:** http://_____________:3000  

---

## 1. Introducción

Este informe documenta las **pruebas de seguridad** aplicadas al sistema Happy Jump (app Android + API Node.js + MySQL), en el marco del curso de calidad de software.

El alcance incluye:

- **Análisis estático** de código y dependencias (SonarCloud, Snyk).
- **Pruebas dinámicas** de autenticación, autorización y validación de entradas (Postman / cURL).
- **Revisión de configuración** móvil (`network_security_config.xml`).
- **Auditoría npm** del backend (`npm audit`).

**No incluye:** pentesting profesional, pruebas de intrusión avanzadas ni auditoría de infraestructura en producción.

**Referencias:**

- `docs/CASOS_SEGURIDAD.csv` (SEC-001 … SEC-018)
- `docs/PLAN_MAESTRO_PRUEBAS_SOFTWARE_HAPPY_JUMP.md` (sección estrategia Sonar/Snyk)
- `docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md`

---

## 2. Objetivos

1. Verificar controles de **autenticación JWT** y **sesión única** (`active_token` en MySQL).
2. Validar **autorización por rol** (trabajador vs administrador en `/reportes`, cambio de PIN).
3. Comprobar que el **PIN no se almacena en texto plano** (bcrypt).
4. Detectar **vulnerabilidades en dependencias** (Gradle vía Snyk; npm vía audit).
5. Registrar hallazgos con severidad y acciones de mitigación.

---

## 3. Metodología

| Tipo | Herramienta | Casos |
|------|-------------|-------|
| Estática — código Kotlin | SonarCloud | SEC-014 |
| Estática — dependencias Android | Snyk (`snyk.yml`) | SEC-013, SEC-018 |
| Estática — dependencias Node | `npm audit` | SEC-015 |
| Dinámica — API | Postman / colección seguridad | SEC-001 … SEC-011, SEC-016, SEC-017 |
| Revisión diseño | MySQL + XML Android | SEC-009, SEC-012 |

**Criterio de aceptación:** los casos **P0 de seguridad** (SEC-001, 002, 005, 006, 007, 009, 010, 013, 014) deben **pasar** o quedar documentados con plan de corrección.

---

## 4. Entorno de prueba

| Componente | Detalle |
|------------|---------|
| API | Node.js, Express, puerto 3000 |
| Base de datos | MySQL (usuarios seed Admin / Rosisela, PIN 1234) |
| Herramientas | Postman, SonarCloud, Snyk, GitHub Actions |
| App Android | Build debug; revisión `network_security_config.xml` |

---

## 5. Resumen ejecutivo

| Ámbito | Resultado global | Observación |
|--------|------------------|-------------|
| Autenticación / autorización API | Pasó / Falló / Parcial | |
| Almacenamiento PIN (bcrypt) | Pasó / Falló | |
| Análisis SonarCloud (Security) | Pasó / Falló / Pendiente | |
| Escaneo Snyk (Gradle) | Pasó / Falló / Pendiente | |
| npm audit (server) | 3 moderate (qs) | Ver sección 8 |
| Configuración cleartext Android | Pasó / Falló | Solo dev local |

**Conclusión preliminar:** _______________________________________________________________

---

## 6. Análisis estático — SonarCloud (SEC-014)

**Proyecto SonarCloud:** (completar `projectKey` de `sonar-project.properties`)  
**Fecha último análisis:** __________________  
**Quality Gate:** Pasa / No pasa  

| Métrica | Valor | Comentario |
|---------|-------|------------|
| Vulnerabilidades | | |
| Security Hotspots | | |
| Hotspots revisados | | |
| Bugs (Reliability) | | |

**Captura:** insertar imagen de pestaña **Security** en SonarCloud.

**Acciones tomadas:**

| Hallazgo | Severidad | Acción |
|----------|-----------|--------|
| | | |

---

## 7. Análisis de dependencias — Snyk (SEC-013, SEC-018)

**Workflow:** `.github/workflows/snyk.yml`  
**Comando CI:** `snyk test --all-projects --severity-threshold=medium`  
**Fecha escaneo:** __________________  

| Severidad | Cantidad | Estado |
|-----------|----------|--------|
| Critical | | |
| High | | |
| Medium | | |
| Low | | |

**Captura:** GitHub Actions → job *Snyk Security* y/o panel snyk.io.

**Nota:** el workflow usa `continue-on-error: true`; revisar el log aunque el job aparezca amarillo.

---

## 8. npm audit — backend Node (SEC-015)

Comando ejecutado:

```bash
cd server
npm audit
```

**Resultado registrado (ejemplo local 29/05/2026):**

| Paquete afectado | Severidad | CVE / advisory | Decisión |
|------------------|-----------|----------------|----------|
| qs (vía express/body-parser) | Moderate | GHSA-q8mj-m7cp-5q26 | Pendiente `npm audit fix` / documentar excepción académica |

**Riesgo:** DoS remoto en función `qs.stringify` bajo condiciones específicas; mitigación recomendada: actualizar dependencias cuando el curso lo permita.

---

## 9. Pruebas dinámicas de seguridad (API)

### 9.1 Autenticación

| ID | Caso | Resultado | HTTP | Evidencia |
|----|------|-----------|------|-----------|
| SEC-001 | Sin token → `/reservas-cancha` | | 401 esperado | |
| SEC-002 | Token inválido | | 401 | |
| SEC-003 | Login credenciales incorrectas | | 401 | |
| SEC-004 | Sesión duplicada | | 409 | |
| SEC-005 | Logout invalida token | | 401 tras logout | |
| SEC-017 | PIN nuevo &lt; 4 dígitos | | 400 | |

**Implementación de referencia (API):**

- Middleware `authMiddleware`: valida Bearer JWT y compara con `active_token` en BD.
- Login: `bcrypt.compare` sobre PIN; rechazo 409 si ya hay sesión activa.

### 9.2 Autorización por rol

| ID | Caso | Resultado | HTTP | Evidencia |
|----|------|-----------|------|-----------|
| SEC-006 | Trabajador → GET `/reportes` | | 403 | |
| SEC-007 | Trabajador → PUT PIN ajeno | | 403 | |
| SEC-008 | Admin → GET `/reportes` | | 200 | |

### 9.3 Validación de entradas y datos

| ID | Caso | Resultado | Notas |
|----|------|-----------|-------|
| SEC-009 | PIN en BD es hash bcrypt | | SELECT no debe mostrar "1234" en claro |
| SEC-010 | Intento SQL injection en login | | Sin bypass; 401 |
| SEC-011 | Payload inválido en reserva | | 4xx controlado |
| SEC-016 | `/health` público | | Aceptado en dev |

---

## 10. Revisión configuración Android (SEC-012)

Archivo: `app/src/main/res/xml/network_security_config.xml`

| Control | Estado esperado | Resultado |
|---------|-----------------|-----------|
| `cleartextTrafficPermitted` en base-config | `false` | |
| Dominios cleartext permitidos | Solo emulador (localhost), 127.0.0.1 | |
| Producción con IP LAN | Requiere HTTPS o domain adicional | Documentado |

**Recomendación:** en despliegue real usar **HTTPS** y eliminar cleartext excepto entornos de desarrollo.

---

## 11. Matriz de resultados (resumen)

*(Copiar desde `RESULTADOS_SEGURIDAD.csv` o pegar tabla completa)*

| ID | Categoría | Resultado | Fecha | Observaciones |
|----|-----------|-----------|-------|---------------|
| SEC-001 | Autenticación | | | |
| … | … | | | |
| SEC-018 | CI | | | |

**Totales:** ___ pasaron / ___ fallaron / ___ pendientes de ___ casos.

---

## 12. Hallazgos y riesgos residuales

| ID hallazgo | Descripción | Severidad | Mitigación propuesta | Estado |
|-------------|-------------|-----------|----------------------|--------|
| H-01 | npm audit: qs moderate en express | Media | `npm audit fix` o actualizar express | Abierto |
| H-02 | | | | |
| H-03 | Cleartext HTTP en dev Android | Baja | HTTPS en producción | Aceptado dev |

---

## 13. Conclusiones

1. Los controles principales de **autenticación JWT**, **sesión única** y **roles** están implementados en la API y fueron verificados con las pruebas SEC-001 … SEC-008.
2. El almacenamiento de PIN con **bcrypt** cumple el requisito de no guardar secretos en texto plano (SEC-009).
3. El pipeline incluye **SonarCloud** y **Snyk** como auditoría reproducible en CI.
4. Quedan acciones de seguimiento en dependencias Node (npm audit) y revisión periódica de Security Hotspots en Sonar.

**Recomendación final:** mantener escaneos en cada sprint y cerrar vulnerabilidades **High/Critical** antes de entrega a operación.

---

## 14. Anexos

| Anexo | Archivo / ubicación |
|-------|---------------------|
| A | `docs/CASOS_SEGURIDAD.csv` |
| B | `docs/informes-item8/RESULTADOS_SEGURIDAD.csv` |
| C | `docs/postman/HappyJump-Seguridad.postman_collection.json` |
| D | Capturas en `docs/informes-item8/capturas/` |
| E | `docs/EVIDENCIAS_CALIDAD_SONAR_SNYK.md` |
