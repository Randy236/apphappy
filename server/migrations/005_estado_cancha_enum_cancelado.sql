-- La API guarda cancelaciones con estado = 'cancelado'.
-- Bases antiguas solo tenían ENUM('ocupado','con_adelanto') → error "Data truncated for column 'estado'".

USE happy_jump;

ALTER TABLE reservas_cancha
  MODIFY COLUMN estado ENUM('ocupado', 'con_adelanto', 'cancelado') NOT NULL DEFAULT 'ocupado';
