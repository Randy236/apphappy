-- Demo / presentación: deja ventas e ingresos en 0 (como si no hubiera habido reservas).
-- También libera sesiones para que no salga "otro dispositivo".
-- Ejecutar: npm run demo:reset-ventas  (Laragon/MySQL encendido)
-- CUIDADO: borra TODAS las reservas de cancha y salones.

USE happy_jump;

DELETE FROM reservas_cancha;
DELETE FROM reservas_salones;

UPDATE usuarios SET active_token = NULL;
