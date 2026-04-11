-- Usuario trabajador Rosisela (PIN 1234) y liberar sesión de Juan Perez si quedó bloqueado.
-- mysql -u root -p happy_jump < migrations/002_usuario_rosisela.sql

USE happy_jump;

UPDATE usuarios SET active_token = NULL WHERE nombre = 'Juan Perez';

INSERT INTO usuarios (nombre, pin, rol)
VALUES (
  'Rosisela',
  '$2a$10$7Do0HIhzOX7Y6ckpSlvDC./CXtdUJybUB1wWr5GE18bdY.Au4TiQm',
  'trabajador'
)
ON DUPLICATE KEY UPDATE
  pin = VALUES(pin),
  active_token = NULL;
