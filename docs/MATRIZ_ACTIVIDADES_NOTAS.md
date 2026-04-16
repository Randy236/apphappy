# Notas para la matriz — Happy Jump

## Texto para la franja azul (reemplaza el ejemplo ACME)

**Happy Jump** es un centro de entretenimiento y eventos que ofrece **canchas deportivas** (fútbol y vóley) y **salones** para fiestas y eventos. El personal usa una **aplicación Android** enlazada a un **servidor Node.js** y **base MySQL** para registrar reservas, controlar pagos y adelantos, consultar reportes y administrar usuarios con inicio de sesión por **nombre y PIN**.

## Archivo principal

Abre en Excel o LibreOffice: **`MATRIZ_ACTIVIDADES_REQUERIMIENTOS.csv`**.

- Ajusta anchos de columna y, si tu plantilla pide **título en celdas combinadas**, copia el contenido desde la fila 2 en adelante a tu formato institucional.
- La **fila 2** del CSV es solo recordatorio: puedes moverla al encabezado del documento Word/PPT.

## Relación con tus otros documentos

| Matriz (RF) | Requerimientos existentes (REQ) |
|-------------|----------------------------------|
| RF01 | REQ-001 |
| RF02 | REQ-002 REQ-003 |
| RF03 | REQ-004 |
| RF04 | REQ-005 |
| RF05–RF11 | REQ-007 REQ-008 REQ-012 REQ-013 + mejoras (calendario turno único Otra hora) |
| RF12–RF13 | REQ-009 |
| RF14–RF15 | REQ-010 |
| RF16 | REQ-011 |
| RF17 | REQ-006 REQ-012 REQ-013 |
| RF18 | Mejora API duración (listado con `duracion_minutos`) |

**Casos de prueba:** enlaza en tu presentación los **CP-001 … CP-014** de `CASOS_DE_PRUEBA.csv` con los **CUS-01 … CUS-18** (puedes usar solo un subconjunto si el profesor pide menos filas).

## Actores (resumen)

- **Trabajador:** opera cancha y salones en el día a día.  
- **Administrador:** reportes, funciones ampliadas, gestión.  
- **Sistema (API + MySQL):** validaciones persistencia y reglas de negocio.  
- **Cliente:** no usa la app; sus datos los ingresa el personal (actor indirecto en casos de uso).
