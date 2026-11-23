-- Point teacher.user_id foreign key to `users` table (drop legacy constraints safely)

SET @fk1 := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'teacher'
      AND CONSTRAINT_NAME = 'FKpb6g6pahj1mr2ijg92r7m1xlh'
    LIMIT 1
);
SET @stmt1 := IF(@fk1 IS NOT NULL, 'ALTER TABLE teacher DROP FOREIGN KEY FKpb6g6pahj1mr2ijg92r7m1xlh', 'SELECT 1');
PREPARE s1 FROM @stmt1; EXECUTE s1; DEALLOCATE PREPARE s1;

SET @fk2 := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'teacher'
      AND CONSTRAINT_NAME = 'FKcp1vpkh4bh0qux9vtvs0fkwrn'
    LIMIT 1
);
SET @stmt2 := IF(@fk2 IS NOT NULL, 'ALTER TABLE teacher DROP FOREIGN KEY FKcp1vpkh4bh0qux9vtvs0fkwrn', 'SELECT 1');
PREPARE s2 FROM @stmt2; EXECUTE s2; DEALLOCATE PREPARE s2;

-- Add FK if missing
SET @fk_users := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'teacher'
      AND REFERENCED_TABLE_NAME = 'users'
    LIMIT 1
);
SET @stmt3 := IF(@fk_users IS NULL, 'ALTER TABLE teacher ADD CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id)', 'SELECT 1');
PREPARE s3 FROM @stmt3; EXECUTE s3; DEALLOCATE PREPARE s3;
