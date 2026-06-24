-- Tabla de auditoría: registra crear, editar, eliminar, login, etc.
-- mysql -u root happy_jump < migrations/010_auditoria.sql

USE happy_jump;

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
