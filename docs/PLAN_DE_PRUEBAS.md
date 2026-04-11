# Plan de pruebas — Happy Jump

**Proyecto:** aplicación móvil Android (Kotlin/Compose) + API Node.js + MySQL  
**Versión del documento:** 1.0  
**Alcance:** validación funcional, integración app–API y comprobaciones básicas de seguridad/usabilidad en entornos de desarrollo y staging.

---

## 1. Objetivos

- Verificar que los requisitos funcionales acordados se cumplan de forma repetible.
- Detectar defectos antes de entregas o demos al cliente.
- Documentar evidencia (casos ejecutados, resultado, fecha, versión).

## 2. Alcance y fuera de alcance

| Incluido | Excluido (salvo acuerdo) |
|----------|---------------------------|
| Login, roles (trabajador/admin), sesión | Pruebas de carga/stress masivas |
| Módulos Cancha y Salones (CRUD/reservas según diseño) | Certificación en tiendas (Google Play) |
| Reportes y gráficos si existen en la build | Pruebas en todos los modelos de dispositivo |
| API REST + validaciones en servidor | Pentesting profesional |

## 3. Criterios de entrada

- Build instalable (debug/release) y API desplegada con base de datos migrada.
- Credenciales de prueba disponibles (p. ej. Admin / trabajador).
- `local.properties` o URL de API apuntando al entorno correcto.

## 4. Criterios de salida

- Casos de prueba críticos (P0/P1) ejecutados sin bloqueos.
- Defectos críticos cerrados o aceptados con plan de mitigación.
- Registro de ejecución (fecha, responsable, versión app/API).

## 5. Entornos

| Entorno | Uso |
|---------|-----|
| Desarrollo local | Desarrollo y smoke tests |
| Wi‑Fi / USB (adb reverse) | Pruebas en dispositivo físico |
| Staging (si existe) | Validación previa a demo |

## 6. Tipos de prueba

| Tipo | Descripción |
|------|-------------|
| Funcional | Flujos de negocio según requisitos |
| Integración | App ↔ API ↔ MySQL |
| Regresión | Tras cambios en Cancha/Salones/Auth |
| Usabilidad básica | Navegación, mensajes de error comprensibles |

## 7. Roles y responsabilidades

| Rol | Responsabilidad |
|-----|-----------------|
| Tester / desarrollador | Ejecutar casos, registrar resultados |
| Product owner / profesor | Aceptar alcance y prioridades |

## 8. Riesgos y dependencias

- Cambio de IP/red: actualizar URL de API y reinstalar app.
- Sesión única por usuario: bloqueos al probar dos dispositivos con el mismo usuario.
- Recursos del PC: builds Gradle pueden fallar por RAM insuficiente.

## 9. Herramientas

- Android Studio, dispositivo o emulador.
- Postman/cURL o navegador para `/health` y endpoints.
- SonarQube Cloud (calidad de código; complementario al plan funcional).

## 10. Calendario sugerido (adaptable)

| Fase | Actividad |
|------|-----------|
| Semana 1 | Smoke + login + un flujo Cancha |
| Semana 2 | Salones + reportes + regresión |
| Cierre | Documento de resultados y backlog de mejoras |

---

## Anexo: trazabilidad

Los requisitos detallados están en `REQUERIMIENTOS.csv`.  
Los casos ejecutables están en `CASOS_DE_PRUEBA.csv`.
