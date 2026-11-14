-- Ensure enrollment.status can store enum names from EnrollmentStatus
-- (ENROLLED, COMPLETED, DROPPED) on already-versioned databases.

SET @enrollment_tbl_exists := (
    SELECT COUNT(*)
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'enrollment'
);

SET @stmt_alter_status := IF(
    @enrollment_tbl_exists = 1,
    'ALTER TABLE enrollment MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT ''ENROLLED''',
    'SELECT 1'
);
PREPARE s_alter_status FROM @stmt_alter_status;
EXECUTE s_alter_status;
DEALLOCATE PREPARE s_alter_status;

SET @stmt_normalize_status := IF(
    @enrollment_tbl_exists = 1,
    'UPDATE enrollment SET status = CASE
        WHEN status IS NULL OR TRIM(CAST(status AS CHAR)) = '''' OR TRIM(CAST(status AS CHAR)) = ''0'' THEN ''ENROLLED''
        WHEN UPPER(TRIM(CAST(status AS CHAR))) IN (''1'', ''COMPLETED'') THEN ''COMPLETED''
        WHEN UPPER(TRIM(CAST(status AS CHAR))) IN (''2'', ''DROPPED'') THEN ''DROPPED''
        ELSE UPPER(TRIM(CAST(status AS CHAR)))
    END',
    'SELECT 1'
);
PREPARE s_normalize_status FROM @stmt_normalize_status;
EXECUTE s_normalize_status;
DEALLOCATE PREPARE s_normalize_status;
