-- Normalize to table `users` and ensure foreign keys point to it

-- If `user` exists (old name) and `users` does not, rename
SET @has_user := (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user');
SET @has_users := (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users');
SET @stmt := IF(@has_user = 1 AND @has_users = 0, 'RENAME TABLE `user` TO `users`', 'SELECT 1');
PREPARE s1 FROM @stmt; EXECUTE s1; DEALLOCATE PREPARE s1;

-- Drop teacher foreign keys not pointing to `users`
SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'teacher' AND REFERENCED_TABLE_NAME <> 'users' LIMIT 1);
SET @stmt_fk := IF(@fk IS NOT NULL, CONCAT('ALTER TABLE teacher DROP FOREIGN KEY ', @fk), 'SELECT 1');
PREPARE s2 FROM @stmt_fk; EXECUTE s2; DEALLOCATE PREPARE s2;

-- Drop student foreign keys not pointing to `users`
SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'student' AND REFERENCED_TABLE_NAME <> 'users' LIMIT 1);
SET @stmt_fk := IF(@fk IS NOT NULL, CONCAT('ALTER TABLE student DROP FOREIGN KEY ', @fk), 'SELECT 1');
PREPARE s3 FROM @stmt_fk; EXECUTE s3; DEALLOCATE PREPARE s3;

-- Recreate FKs to `users` only if missing (avoid duplicate constraint names)
SET @teacher_fk := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'teacher'
      AND REFERENCED_TABLE_NAME = 'users'
    LIMIT 1
);
SET @stmt_fk_teacher := IF(@teacher_fk IS NULL, 'ALTER TABLE teacher ADD CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id)', 'SELECT 1');
PREPARE s4 FROM @stmt_fk_teacher; EXECUTE s4; DEALLOCATE PREPARE s4;

SET @student_fk := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND REFERENCED_TABLE_NAME = 'users'
    LIMIT 1
);
SET @stmt_fk_student := IF(@student_fk IS NULL, 'ALTER TABLE student ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id)', 'SELECT 1');
PREPARE s5 FROM @stmt_fk_student; EXECUTE s5; DEALLOCATE PREPARE s5;
