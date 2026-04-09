-- Ejecutar una vez si tu base ya existía antes de esta función:
-- mysql -u root -p happy_jump < migrations/001_usuarios_active_token.sql

USE happy_jump;

ALTER TABLE usuarios
  ADD COLUMN active_token VARCHAR(600) NULL DEFAULT NULL
  AFTER rol;
