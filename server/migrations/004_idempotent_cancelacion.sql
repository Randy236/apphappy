-- Alinea tablas con el código actual si faltan columnas (BD creada antes de 003).
-- Seguro de ejecutar varias veces.

USE happy_jump;

SET @dbname = DATABASE();

-- reservas_cancha.motivo_cancelacion
SET @c = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'reservas_cancha' AND COLUMN_NAME = 'motivo_cancelacion'
);
SET @sql = IF(@c = 0,
  'ALTER TABLE reservas_cancha ADD COLUMN motivo_cancelacion VARCHAR(500) NULL DEFAULT NULL AFTER estado',
  'SELECT ''reservas_cancha.motivo_cancelacion ya existe'' AS info'
);
PREPARE s FROM @sql;
EXECUTE s;
DEALLOCATE PREPARE s;

-- Quitar UNIQUE (fecha, hora) si sigue existiendo (permite re-reservar slot cancelado)
SET @i = (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'reservas_cancha' AND INDEX_NAME = 'uq_cancha_fecha_hora'
);
SET @sql2 = IF(@i > 0,
  'ALTER TABLE reservas_cancha DROP INDEX uq_cancha_fecha_hora',
  'SELECT ''índice uq_cancha_fecha_hora ya no existe'' AS info'
);
PREPARE s2 FROM @sql2;
EXECUTE s2;
DEALLOCATE PREPARE s2;

-- reservas_salones.cancelada + motivo_cancelacion
SET @c2 = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'reservas_salones' AND COLUMN_NAME = 'cancelada'
);
SET @sql3 = IF(@c2 = 0,
  'ALTER TABLE reservas_salones ADD COLUMN cancelada TINYINT(1) NOT NULL DEFAULT 0 AFTER adelanto',
  'SELECT ''reservas_salones.cancelada ya existe'' AS info'
);
PREPARE s3 FROM @sql3;
EXECUTE s3;
DEALLOCATE PREPARE s3;

SET @c3 = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'reservas_salones' AND COLUMN_NAME = 'motivo_cancelacion'
);
SET @sql4 = IF(@c3 = 0,
  'ALTER TABLE reservas_salones ADD COLUMN motivo_cancelacion VARCHAR(500) NULL DEFAULT NULL AFTER cancelada',
  'SELECT ''reservas_salones.motivo_cancelacion ya existe'' AS info'
);
PREPARE s4 FROM @sql4;
EXECUTE s4;
DEALLOCATE PREPARE s4;

-- ENUM estado debe incluir 'cancelado' (BD antigua sin migración 003 completa).
ALTER TABLE reservas_cancha
  MODIFY COLUMN estado ENUM('ocupado', 'con_adelanto', 'cancelado') NOT NULL DEFAULT 'ocupado';
