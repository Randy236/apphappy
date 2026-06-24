-- Happy Jump — tablas para Railway (sin CREATE DATABASE)
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

CREATE TABLE IF NOT EXISTS auditoria (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  fecha_hora TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  usuario_id INT NULL,
  usuario_nombre VARCHAR(100) NULL,
  accion VARCHAR(50) NOT NULL,
  tabla VARCHAR(80) NOT NULL,
  registro_id INT NULL,
  detalle JSON NULL,
  endpoint VARCHAR(200) NULL,
  ip_origen VARCHAR(45) NULL,
  INDEX idx_auditoria_fecha (fecha_hora),
  INDEX idx_auditoria_tabla (tabla, registro_id),
  INDEX idx_auditoria_usuario (usuario_id),
  INDEX idx_auditoria_accion (accion)
);
