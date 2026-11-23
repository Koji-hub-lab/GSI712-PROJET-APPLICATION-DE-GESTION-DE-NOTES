-- Normalize enrollment.status to string enum names used by EnrollmentStatus:
-- ENROLLED, COMPLETED, DROPPED.
-- This handles legacy schemas where status was INT/ENUM with incompatible values.

ALTER TABLE enrollment DROP COLUMN IF EXISTS status_tmp;
ALTER TABLE enrollment ADD COLUMN status_tmp VARCHAR(20) NULL;

UPDATE enrollment
SET status_tmp = CASE
    WHEN status IS NULL THEN 'ENROLLED'
    WHEN UPPER(TRIM(CAST(status AS CHAR))) IN ('0', 'ENROLLED') THEN 'ENROLLED'
    WHEN UPPER(TRIM(CAST(status AS CHAR))) IN ('1', 'COMPLETED') THEN 'COMPLETED'
    WHEN UPPER(TRIM(CAST(status AS CHAR))) IN ('2', 'DROPPED') THEN 'DROPPED'
    ELSE 'ENROLLED'
END;

ALTER TABLE enrollment DROP COLUMN status;
ALTER TABLE enrollment CHANGE COLUMN status_tmp status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED';
