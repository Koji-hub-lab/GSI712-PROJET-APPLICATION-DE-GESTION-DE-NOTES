-- Force student.user_id FK to reference `users` table (handles leftover FK name FKk5m148xqefonqw7bgnpm0snwj)

-- Rename legacy `user` table to `users` if `users` is missing
SET @has_user := (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user');
SET @has_users := (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users');
SET @stmt := IF(@has_user = 1 AND @has_users = 0, 'RENAME TABLE `user` TO `users`', 'SELECT 1');
PREPARE s1 FROM @stmt; EXECUTE s1; DEALLOCATE PREPARE s1;

-- Drop the known bad FK if it exists (points to `user`)
SET @drop_fk := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND CONSTRAINT_NAME = 'FKk5m148xqefonqw7bgnpm0snwj'
    LIMIT 1
);
SET @stmt_drop_fk := IF(@drop_fk IS NOT NULL, 'ALTER TABLE student DROP FOREIGN KEY FKk5m148xqefonqw7bgnpm0snwj', 'SELECT 1');
PREPARE s2 FROM @stmt_drop_fk; EXECUTE s2; DEALLOCATE PREPARE s2;

-- Ensure FK exists to `users`
SET @fk_users := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND REFERENCED_TABLE_NAME = 'users'
    LIMIT 1
);
SET @stmt_fk_users := IF(@fk_users IS NULL, 'ALTER TABLE student ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id)', 'SELECT 1');
PREPARE s3 FROM @stmt_fk_users; EXECUTE s3; DEALLOCATE PREPARE s3;
