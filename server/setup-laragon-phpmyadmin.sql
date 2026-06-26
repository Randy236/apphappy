-- Happy Jump — instalación completa para Laragon + phpMyAdmin
-- Importar este archivo UNA vez: phpMyAdmin → Importar → elegir este archivo → Continuar

CREATE DATABASE IF NOT EXISTS happy_jump CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE happy_jump;

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  pin VARCHAR(255) NOT NULL,
  rol ENUM('trabajador', 'administrador') NOT NULL DEFAULT 'trabajador',
  active_token VARCHAR(600) NULL DEFAULT NULL,
  deleted_at DATETIME NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS reservas_cancha (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombreCliente VARCHAR(200) NOT NULL,
  deporte ENUM('Futbol', 'Voley') NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  montoTotal DECIMAL(10,2) NOT NULL,
  adelanto DECIMAL(10,2) NOT NULL DEFAULT 0,
  estado ENUM('ocupado', 'con_adelanto', 'cancelado') NOT NULL DEFAULT 'ocupado',
  motivo_cancelacion VARCHAR(500) NULL DEFAULT NULL,
  duracion_minutos SMALLINT NULL DEFAULT NULL,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL
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
  cancelada TINYINT(1) NOT NULL DEFAULT 0,
  motivo_cancelacion VARCHAR(500) NULL DEFAULT NULL,
  salon VARCHAR(120) NOT NULL,
  fecha DATE NOT NULL,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL
);

-- Usuario trabajador Rosisela (PIN 1234). Admin se crea al arrancar la API si la tabla está vacía.
INSERT INTO usuarios (nombre, pin, rol)
VALUES (
  'Rosisela',
  '$2a$10$7Do0HIhzOX7Y6ckpSlvDC./CXtdUJybUB1wWr5GE18bdY.Au4TiQm',
  'trabajador'
)
ON DUPLICATE KEY UPDATE pin = VALUES(pin), active_token = NULL;

UPDATE usuarios SET active_token = NULL;
