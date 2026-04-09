# Happy Jump App

Aplicacion movil Android para gestion de reservas de cancha y salones, con roles de trabajador y administrador.

## Stack

- Android: Kotlin + Jetpack Compose
- API: Node.js + Express
- Base de datos: MySQL

## Modulos principales

- Login por nombre + PIN
- Cancha: reservas por horario, estados y detalle
- Salones: reservas y disponibilidad (admin en solo lectura)
- Reportes: grafico de ingresos por area y resumen por periodo
- Perfil: cambio de PIN, historial y cierre de sesion
- Sesion unica por usuario (bloquea login en otro dispositivo activo)

## Requisitos

- JDK 11+
- Android SDK
- Node.js 18+
- MySQL 8+

## Configuracion rapida

### 1) Base de datos

Ejecutar:

- `server/schema.sql`

Si tu base ya existia antes de la sesion unica, ejecutar tambien:

- `server/migrations/001_usuarios_active_token.sql`

### 2) Backend

En `server/`:

```bash
npm install
npm start
```

API por defecto: `http://0.0.0.0:3000`

### 3) App Android

Definir `happyJump.api.baseUrl` en `local.properties`:

- Emulador: `http://10.0.2.2:3000/`
- Celular fisico (misma Wi-Fi): `http://IP_DE_TU_PC:3000/`

Compilar/instalar:

```bash
./gradlew :app:installDebug
```

## Credenciales demo (seed)

- Administrador: `Admin` / PIN `1234`
- Trabajador: `Juan Perez` / PIN `1234`

## Flujo sugerido para avances en GitHub

```bash
git add .
git commit -m "feat: descripcion corta del avance"
git push
```

Tipos recomendados de commit:

- `feat:` nueva funcionalidad
- `fix:` correccion
- `refactor:` mejora interna
- `test:` pruebas
- `docs:` documentacion

