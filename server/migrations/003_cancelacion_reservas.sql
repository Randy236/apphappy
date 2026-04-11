-- Cancelación de reservas (cancha y salones) con motivo obligatorio.
-- La UNIQUE (fecha, hora) impedía volver a reservar un slot cancelado; se elimina
-- y el solape se valida en la API excluyendo filas canceladas.

USE happy_jump;

ALTER TABLE reservas_cancha
  MODIFY COLUMN estado ENUM('ocupado', 'con_adelanto', 'cancelado') NOT NULL DEFAULT 'ocupado',
  ADD COLUMN motivo_cancelacion VARCHAR(500) NULL DEFAULT NULL AFTER estado;

ALTER TABLE reservas_cancha DROP INDEX uq_cancha_fecha_hora;

ALTER TABLE reservas_salones
  ADD COLUMN cancelada TINYINT(1) NOT NULL DEFAULT 0 AFTER adelanto,
  ADD COLUMN motivo_cancelacion VARCHAR(500) NULL DEFAULT NULL AFTER cancelada;
