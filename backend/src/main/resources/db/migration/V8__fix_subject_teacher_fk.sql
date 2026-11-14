-- Ensure subject.teacher_id points to `users` table (not legacy `teacher`)

-- Drop existing FK if it does not reference `users`
SET @fk := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'subject'
      AND REFERENCED_TABLE_NAME <> 'users'
    LIMIT 1
);
SET @stmt := IF(@fk IS NOT NULL, CONCAT('ALTER TABLE subject DROP FOREIGN KEY ', @fk), 'SELECT 1');
PREPARE s1 FROM @stmt; EXECUTE s1; DEALLOCATE PREPARE s1;

-- Add FK to users if missing
SET @fk_users := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'subject'
      AND REFERENCED_TABLE_NAME = 'users'
    LIMIT 1
);
SET @stmt2 := IF(@fk_users IS NULL, 'ALTER TABLE subject ADD CONSTRAINT fk_subject_teacher FOREIGN KEY (teacher_id) REFERENCES users(id)', 'SELECT 1');
PREPARE s2 FROM @stmt2; EXECUTE s2; DEALLOCATE PREPARE s2;
