-- Remove legacy foreign keys from enrollment that still point to `students` (plural).
-- Current model uses `student` table.

SET @drop_bad_fk_sql := (
    SELECT IFNULL(
        CONCAT(
            'ALTER TABLE enrollment ',
            GROUP_CONCAT(CONCAT('DROP FOREIGN KEY `', CONSTRAINT_NAME, '`') SEPARATOR ', ')
        ),
        'SELECT 1'
    )
    FROM (
        SELECT DISTINCT CONSTRAINT_NAME
        FROM information_schema.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'enrollment'
          AND REFERENCED_TABLE_NAME = 'students'
    ) t
);

PREPARE s_drop_bad_fk FROM @drop_bad_fk_sql;
EXECUTE s_drop_bad_fk;
DEALLOCATE PREPARE s_drop_bad_fk;
