-- Point student.user_id foreign key to `users` table (drop legacy constraint safely)

SET @fk := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND CONSTRAINT_NAME = 'FKk0thg920a3xk3v59yjbsatw1l'
    LIMIT 1
);
SET @stmt := IF(@fk IS NOT NULL, 'ALTER TABLE student DROP FOREIGN KEY FKk0thg920a3xk3v59yjbsatw1l', 'SELECT 1');
PREPARE s1 FROM @stmt; EXECUTE s1; DEALLOCATE PREPARE s1;

-- Add FK if missing
SET @fk_users := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND REFERENCED_TABLE_NAME = 'users'
    LIMIT 1
);
SET @stmt2 := IF(@fk_users IS NULL, 'ALTER TABLE student ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id)', 'SELECT 1');
PREPARE s2 FROM @stmt2; EXECUTE s2; DEALLOCATE PREPARE s2;
