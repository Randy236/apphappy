-- Happy Jump — esquema MySQL
CREATE DATABASE IF NOT EXISTS happy_jump CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE happy_jump;

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  pin VARCHAR(255) NOT NULL,
  rol ENUM('trabajador', 'administrador') NOT NULL DEFAULT 'trabajador',
  active_token VARCHAR(600) NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS reservas_cancha (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombreCliente VARCHAR(200) NOT NULL,
  deporte ENUM('Futbol', 'Voley') NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  montoTotal DECIMAL(10,2) NOT NULL,
  adelanto DECIMAL(10,2) NOT NULL DEFAULT 0,
  estado ENUM('ocupado', 'con_adelanto') NOT NULL DEFAULT 'ocupado',
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_cancha_fecha_hora (fecha, hora)
);

CREATE TABLE IF NOT EXISTS reservas_salones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombreCliente VARCHAR(200) NOT NULL,
  tipoEvento ENUM('Cumpleanos', 'Otro') NOT NULL,
  nombreCumpleanero VARCHAR(200) NULL,
  zona VARCHAR(120) NOT NULL,
  numeroNinos INT NOT NULL DEFAULT 0,
  horaInicio TIME NOT NULL,
  horaFin TIME NOT NULL,
  precioTotal DECIMAL(10,2) NOT NULL,
  adelanto DECIMAL(10,2) NOT NULL DEFAULT 0,
  salon VARCHAR(120) NOT NULL,
  fecha DATE NOT NULL,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
