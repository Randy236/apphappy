# MANUAL DE USUARIO — Happy Jump

**Versión del documento:** 1.0  
**Fecha:** __________________  
**Proyecto:** Happy Jump — Sistema de reservas (cancha y salones)  
**Plataforma:** Aplicación Android  
**Repositorio:** https://github.com/Randy236/apphappy  

---

## Tabla de contenidos

1. [Introducción](#1-introducción)  
2. [Requisitos para usar la app](#2-requisitos-para-usar-la-app)  
3. [Roles de usuario](#3-roles-de-usuario)  
4. [Inicio de sesión](#4-inicio-de-sesión)  
5. [Pantalla principal y navegación](#5-pantalla-principal-y-navegación)  
6. [Módulo Cancha](#6-módulo-cancha)  
7. [Módulo Salones](#7-módulo-salones)  
8. [Módulo Reportes (administrador)](#8-módulo-reportes-administrador)  
9. [Módulo Perfil](#9-módulo-perfil)  
10. [Notificaciones de cobro pendiente](#10-notificaciones-de-cobro-pendiente)  
11. [Mensajes de error frecuentes](#11-mensajes-de-error-frecuentes)  
12. [Preguntas frecuentes (FAQ)](#12-preguntas-frecuentes-faq)  
13. [Anexos](#13-anexos)  

---

## 1. Introducción

**Happy Jump** es una aplicación móvil para el personal de un centro de entretenimiento y eventos. Permite:

- Registrar y consultar **reservas de cancha** (fútbol y vóley) por horarios de media hora o rangos personalizados.
- Gestionar **reservas de salones** para cumpleaños y otros eventos.
- Consultar **reportes de ingresos** (solo administrador).
- Administrar la **cuenta personal** (cambio de PIN, historial, cierre de sesión).

La app se conecta a un **servidor API** en la red local o en internet. Sin conexión al servidor, no podrá guardar ni consultar reservas.

Figura 1. Pantalla de inicio de sesión.

---

## 2. Requisitos para usar la app

| Requisito | Detalle |
|-----------|---------|
| Dispositivo | Celular o tablet **Android** |
| Conexión | Misma red Wi‑Fi que el servidor, o configuración indicada por el administrador del sistema |
| Servidor API | Debe estar encendido (por defecto puerto **3000**) |
| Usuario | Nombre y **PIN** de 4 a 8 dígitos asignados por el administrador |

**Nota para instalación:** la URL del servidor de producción es `https://happyjump.sorbits.site/` (configurable en `local.properties` al compilar).

---

## 3. Roles de usuario

La app distingue dos roles. En la parte superior verá el badge **“Cuenta: Trabajador”** o **“Cuenta: Administrador”**.

### 3.1 Trabajador

Puede:

- Operar **Cancha** (crear, consultar, cobrar saldo, cancelar reservas).
- Operar **Salones** (crear, consultar, cobrar, cancelar).
- Ver **Perfil** (cambiar PIN, historial, cerrar sesión).

No puede:

- Ver el módulo **Reportes** de ingresos.

### 3.2 Administrador

Puede:

- Todo lo de **Cancha** (igual que el trabajador).
- Ver **Salones** en **solo lectura** (consultar ocupación, sin crear ni cancelar reservas).
- Ver **Reportes** (ingresos, gráficos, PDF, cancelaciones del periodo).
- **Perfil** (cambiar PIN, historial, cerrar sesión).

### 3.3 Usuarios de demostración

| Rol | Nombre de usuario | PIN |
|-----|-------------------|-----|
| Administrador | `Admin` | `1234` |
| Trabajador | `Rosisela` | `1234` |

*(Solo para entornos de prueba; en producción use las credenciales reales.)*

---

## 4. Inicio de sesión

### 4.1 Pasos

1. Abra la aplicación **Happy Jump**.
2. En **“Nombre de usuario”**, escriba su nombre (ejemplo: `Rosisela` o `Admin`).
3. Pulse **Continuar**.
4. Revise el nombre mostrado. Si está mal, pulse **Cambiar** y vuelva al paso 2.
5. En **“PIN (solo números)”**, ingrese su PIN (mínimo 4 dígitos).
6. Pulse **Entrar**.

Si las credenciales son correctas, accederá a la pantalla principal con el saludo **“Hola, {su nombre} 👋”**.

Figura 2. Confirmación de usuario e ingreso de PIN.

### 4.2 Sesión única

Solo puede tener **una sesión activa** por usuario. Si ya inició sesión en otro celular o tablet, verá un mensaje similar a:

> *“Este usuario ya inició sesión en otro celular o tablet…”*

**Solución:** cierre sesión en el otro dispositivo desde **Perfil → Cerrar sesión**, o pida al administrador del sistema que libere la sesión.

### 4.3 Cierre de sesión

Vaya a **Perfil** (última pestaña) y pulse **Cerrar sesión**. Volverá a la pantalla de login.

---

## 5. Pantalla principal y navegación

### 5.1 Barra superior

- **Saludo:** “Hola, {nombre} 👋”
- **Subtítulo:** “Bienvenido a Happy Jump”
- **Tipo de cuenta:** Trabajador o Administrador

### 5.2 Barra inferior (pestañas)

| Pestaña | Trabajador | Administrador |
|---------|------------|---------------|
| **Cancha** | Sí | Sí |
| **Salones** | Sí | Sí (solo consulta) |
| **Reportes** | No | Sí |
| **Perfil** | Sí | Sí |

Figura 3. Barra de navegación inferior.

---

## 6. Módulo Cancha

Use este módulo para la **Cancha Principal**: reservas de fútbol o vóley en franjas de **:00** y **:30**, o en horarios personalizados.

### 6.1 Consultar el día

1. Pulse la pestaña **Cancha**.
2. Verá el título **“Cancha Principal”** y la **grilla de horarios** del día seleccionado.
3. Use la **barra de semana** (flechas ← →) para cambiar de semana.
4. Toque un **día** (Lun, Mar, …) para ver esa fecha.
5. Pulse **Ir a hoy** para volver al día actual.

Figura 4. Grilla horaria de la cancha.

### 6.2 Calendario mensual

1. Pulse el **icono de calendario** (junto a la fecha).
2. Se abre el calendario del mes con colores:

| Color | Significado |
|-------|-------------|
| Verde | Día **libre** (sin reservas o con mucha disponibilidad) |
| Amarillo | **Parcialmente** ocupado |
| Rojo | **Lleno** |
| Gris | Día **pasado** |

3. Use las **flechas** del calendario para cambiar de mes.
4. Toque un día para seleccionarlo y cerrar el calendario.

Figura 5. Calendario mensual con leyenda de colores.

### 6.3 Crear reserva desde la grilla

1. Elija el **día** deseado.
2. Toque una franja **Disponible** (estado libre en la grilla).
3. Se abre **“Registrar Reserva”**.
4. Complete los campos:

| Campo | Descripción |
|-------|-------------|
| Fecha | Día de la reserva (según día seleccionado) |
| Horario de juego | Inicio y fin (formato HH:mm) |
| Nombre del cliente | Persona que reserva |
| Deporte | Fútbol o Vóley |
| Monto total | Precio acordado |
| Forma de pago | **Cancelado** (pago completo) o **Adelanto** (indique monto adelantado) |

5. Pulse **Guardar Reserva**.

La franja aparecerá como **Ocupado**, **Adelanto** u otro estado según el pago.

### 6.4 Reserva con “Otra hora”

Si el cliente necesita un horario que **no coincide** con :00 o :30 (por ejemplo 12:15–12:45):

1. Pulse el botón **Otra hora** (parte superior).
2. Complete el mismo formulario con **inicio y fin exactos**.
3. Pulse **Guardar Reserva**.

**Nota:** no se permiten reservas en fechas u horas ya pasadas (el servidor valida la regla).

### 6.5 Ver detalle de una reserva

1. Toque una franja **ocupada** (Ocupado, Adelanto, Finalizado, etc.).
2. Verá: cliente, deporte, horario, monto total, adelanto y estado.

**Turnos largos:** si una reserva abarca varias medias horas del mismo cliente, la app puede mostrarla como **una sola tarjeta** con hora inicio–fin y duración total.

### 6.6 Cobrar saldo pendiente

Si la reserva tiene **adelanto parcial**:

1. Abra el **detalle** de la reserva.
2. Pulse **Marcar como pagado**.
3. El estado pasa a pagado completo.

### 6.7 Cancelar una reserva

1. Abra el **detalle** de la reserva.
2. Pulse **Cancelar reserva**.
3. Escriba un **motivo** (obligatorio, mínimo 2 caracteres).
4. Pulse **Confirmar cancelación**.

En turnos largos, la cancelación puede aplicar a **todas las franjas** del mismo turno.

### 6.8 Significado de estados en la grilla

| Estado en pantalla | Significado |
|--------------------|-------------|
| Disponible | Horario libre para reservar |
| Ocupado | Reserva con pago completo |
| Adelanto | Reserva con pago parcial |
| Finalizado | Horario ya pasó |
| Finalizado (saldo pendiente) | Pasó el horario pero falta cobrar |
| Pasado | No se puede reservar (hora pasada) |

---

## 7. Módulo Salones

Gestiona reservas de salones para eventos (cumpleaños, fiestas, etc.).

### 7.1 Salones disponibles

- Ex Salón de Pinturas  
- Salón Principal  
- Salón de Eventos Grande  
- Salón Laser  

### 7.2 Trabajador — consultar y reservar

1. Pulse la pestaña **Salones**.
2. Elija un salón de la lista (**Ver horarios y reservas**).
3. Navegue por **semana**, **día** o **calendario** (igual concepto que Cancha).
4. Para **nueva reserva:**
   - Pulse el botón **+** o toque una franja libre.
   - Complete **Nueva Reserva:**
     - Nombre del cliente  
     - Tipo de evento: **Cumpleaños** u **Otro**  
     - Datos del cumpleañero (si aplica)  
     - Zona / número de niños  
     - Horario inicio y fin  
     - Precio y forma de pago (**Cancelado** o **Adelanto**)  
   - Pulse **Guardar Reserva**.
5. Para **detalle, cobro o cancelación:** toque una franja ocupada (igual que en Cancha: **Marcar como pagado**, **Cancelar reserva** con motivo).

Figura 6. Listado de salones y formulario de reserva.

### 7.3 Administrador — solo consulta

El administrador ve un aviso:

> **“Vista solo lectura…”**

Puede **consultar** horarios y reservas existentes, pero **no** puede crear reservas, cobrar saldos ni cancelar desde Salones.

---

## 8. Módulo Reportes (administrador)

Solo visible si inició sesión como **Administrador**.

### 8.1 Consultar ingresos

1. Pulse la pestaña **Reportes**.
2. Elija el **periodo:**
   - **Hoy**
   - **Semana**
   - **Mes**
3. Use las flechas **Anterior / Siguiente** para mover la fecha de referencia.
4. Revise:
   - **Ingresos totales**
   - **Total reservas cancha / salones**
   - Gráfico **Ingresos por área** (cancha y cada salón)

Figura 7. Panel de reportes del administrador.

### 8.2 Exportar PDF

1. En **Reportes**, configure el periodo deseado.
2. Pulse **Guardar reporte en PDF**.
3. Elija la carpeta del dispositivo donde guardar el archivo.

### 8.3 Ver cancelaciones del periodo

1. Pulse **Ver cancelaciones del período**.
2. Se muestra un listado de cancelaciones de **Cancha** y **Salones** con motivos.
3. Si falla la carga, use **Reintentar**.

---

## 9. Módulo Perfil

Disponible para **todos** los usuarios (pestaña **Perfil**).

### 9.1 Datos de la cuenta

Muestra su nombre y rol (Trabajador o Administrador).

### 9.2 Cambiar PIN

1. Expanda la sección **Cambiar PIN**.
2. Ingrese **PIN actual** y **PIN nuevo** (mínimo 4 dígitos).
3. Pulse **Guardar nuevo PIN**.

Figura 8. Pantalla de perfil de usuario.

### 9.3 Historial de reservas

1. Expanda **Historial de reservas**.
2. Verá reservas recientes de **Cancha** y **Salón** (aproximadamente últimos 12 meses).

### 9.4 Cerrar sesión

Pulse **Cerrar sesión** para salir de la app de forma segura.

---

## 10. Notificaciones de cobro pendiente

Si una reserva de cancha terminó con **saldo por cobrar**, la app puede mostrar una **notificación** en el canal **“Cobros pendientes”** para recordarle que debe cobrar al cliente.

- Toque la notificación para ir al detalle de la reserva (según configuración del dispositivo).
- Asegúrese de tener **notificaciones permitidas** para Happy Jump en ajustes del Android.

---

## 11. Mensajes de error frecuentes

| Mensaje | Causa probable | Qué hacer |
|---------|----------------|-----------|
| No se pudo entrar | Nombre o PIN incorrectos | Verifique credenciales |
| Usuario ya activo en otro dispositivo | Sesión única | Cierre sesión en el otro equipo |
| Error de conexión / timeout | API apagada o red incorrecta | Compruebe Wi‑Fi y que el servidor esté encendido |
| No autorizado / sesión no válida | Token expirado o cerró sesión en otro lado | Cierre sesión y vuelva a entrar |
| PIN nuevo inválido | Menos de 4 dígitos | Use PIN de al menos 4 números |
| PIN actual incorrecto | Al cambiar PIN | Verifique el PIN actual |

---

## 12. Preguntas frecuentes (FAQ)

**¿Puedo usar la app sin internet?**  
No. Necesita conexión al servidor API de Happy Jump.

**¿Puedo reservar en el pasado?**  
No. El sistema bloquea fechas y horas ya transcurridas.

**¿El administrador puede crear reservas en Salones?**  
No desde la app; solo consulta. Las reservas de salones las registra el **trabajador**.

**¿Puedo tener la app abierta en dos celulares con el mismo usuario?**  
No al mismo tiempo. Solo una sesión activa por usuario.

**¿Cómo vuelvo al día de hoy en Cancha?**  
Pulse el botón **Ir a hoy**.

---

## 13. Anexos

### Anexo A — Glosario

| Término | Definición |
|---------|------------|
| Adelanto | Pago parcial de una reserva; queda saldo pendiente |
| Cancelado | Pago completo al registrar o cobrar |
| Turno largo | Reserva que ocupa varias franjas de media hora seguidas |
| API | Servidor que guarda los datos de reservas |

### Anexo B — Soporte

Para problemas de acceso, red o usuarios, contacte al **administrador del sistema** o al responsable técnico del proyecto Happy Jump.
