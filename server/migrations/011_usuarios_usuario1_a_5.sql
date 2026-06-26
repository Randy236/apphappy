-- Usuarios de prueba usuarioa..usuarioe (PIN 1234, rol trabajador).
-- mysql -u root -p happy_jump < migrations/011_usuarios_usuario1_a_5.sql

USE happy_jump;

-- PIN 1234 (bcrypt cost 10)
SET @pin_1234 = '$2a$10$7Do0HIhzOX7Y6ckpSlvDC./CXtdUJybUB1wWr5GE18bdY.Au4TiQm';

INSERT INTO usuarios (nombre, pin, rol) VALUES
  ('usuarioa', @pin_1234, 'trabajador'),
  ('usuariob', @pin_1234, 'trabajador'),
  ('usuarioc', @pin_1234, 'trabajador'),
  ('usuariod', @pin_1234, 'trabajador'),
  ('usuarioe', @pin_1234, 'trabajador')
ON DUPLICATE KEY UPDATE
  pin = VALUES(pin),
  rol = VALUES(rol),
  active_token = NULL,
  deleted_at = NULL;
