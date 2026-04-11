-- Permite medias horas que terminan en :30 (ej. 11:00–13:30 → último tramo 13:00–13:30).
USE happy_jump;

ALTER TABLE reservas_cancha
  ADD COLUMN duracion_minutos SMALLINT NULL DEFAULT NULL
  COMMENT 'NULL = inferir por hora (:30→30, resto→60)';
