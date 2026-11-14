-- Align schema for legacy tables that include a NOT NULL username column
ALTER TABLE `users` ADD COLUMN IF NOT EXISTS username VARCHAR(255) NOT NULL DEFAULT '';
