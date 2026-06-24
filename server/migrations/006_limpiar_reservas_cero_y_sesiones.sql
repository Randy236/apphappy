-- Demo / presentación: deja ventas e ingresos en 0 (como si no hubiera habido reservas).
-- También libera sesiones para que no salga "otro dispositivo".
-- Ejecutar: npm run demo:reset-ventas  (Laragon/MySQL encendido)
-- CUIDADO: borra TODAS las reservas de cancha y salones.

USE happy_jump;

-- Eliminado lógico (no DELETE físico)
UPDATE reservas_cancha SET deleted_at = NOW() WHERE deleted_at IS NULL;
UPDATE reservas_salones SET deleted_at = NOW() WHERE deleted_at IS NULL;

UPDATE usuarios SET active_token = NULL;
