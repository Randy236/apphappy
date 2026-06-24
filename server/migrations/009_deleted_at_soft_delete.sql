-- Eliminado lógico en todas las entidades (usuarios, reservas_cancha, reservas_salones).
-- Ejecutar: npm run migrate:009

USE happy_jump;

SET @dbname = DATABASE();

-- usuarios.deleted_at
SET @exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'usuarios' AND COLUMN_NAME = 'deleted_at'
);
SET @sql = IF(@exists = 0,
  'ALTER TABLE usuarios ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL AFTER active_token',
  'SELECT ''usuarios.deleted_at ya existe'' AS info'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- reservas_cancha.deleted_at
SET @exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'reservas_cancha' AND COLUMN_NAME = 'deleted_at'
);
SET @sql = IF(@exists = 0,
  'ALTER TABLE reservas_cancha ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL AFTER createdAt',
  'SELECT ''reservas_cancha.deleted_at ya existe'' AS info'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- reservas_salones.deleted_at
SET @exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'reservas_salones' AND COLUMN_NAME = 'deleted_at'
);
SET @sql = IF(@exists = 0,
  'ALTER TABLE reservas_salones ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL AFTER createdAt',
  'SELECT ''reservas_salones.deleted_at ya existe'' AS info'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
