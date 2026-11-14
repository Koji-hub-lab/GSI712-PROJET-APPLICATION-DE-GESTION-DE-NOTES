-- Ensure 'active' column exists and is non-null with default true (portable syntax)

SET @has_active := (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'active'
);

SET @stmt := IF(@has_active = 0,
    'ALTER TABLE `users` ADD COLUMN active BIT NOT NULL DEFAULT b''1''',
    'SELECT 1');
PREPARE s1 FROM @stmt; EXECUTE s1; DEALLOCATE PREPARE s1;

UPDATE `users` SET active = b'1' WHERE active IS NULL;
