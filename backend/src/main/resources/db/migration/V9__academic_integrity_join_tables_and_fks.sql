-- =============================================================================
-- Academic integrity: join tables and composite FKs so that:
-- 1) Enrollment is valid only when (course_id, subject_id) is in course_subject
--    AND (course_id, student_id) is in course_student (same course).
-- 2) Duplicate enrollments blocked by UNIQUE(student_id, subject_id).
-- =============================================================================

-- 1) Create course_subject (subjects belonging to courses)
CREATE TABLE IF NOT EXISTS course_subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    CONSTRAINT uk_course_subject_course_subject UNIQUE (course_id, subject_id),
    CONSTRAINT fk_course_subject_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_subject_subject FOREIGN KEY (subject_id) REFERENCES subject(id) ON DELETE CASCADE,
    INDEX idx_course_subject_course_id (course_id),
    INDEX idx_course_subject_subject_id (subject_id)
);

-- 2) Create course_student (students enrolled in courses)
CREATE TABLE IF NOT EXISTS course_student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    CONSTRAINT uk_course_student_course_student UNIQUE (course_id, student_id),
    CONSTRAINT fk_course_student_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_student_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    INDEX idx_course_student_course_id (course_id),
    INDEX idx_course_student_student_id (student_id)
);

-- 3) Populate course_subject from existing enrollment data (so composite FK can be added)
INSERT IGNORE INTO course_subject (course_id, subject_id)
SELECT DISTINCT course_id, subject_id FROM enrollment
WHERE course_id IS NOT NULL AND subject_id IS NOT NULL;

-- 4) Populate course_student from existing enrollment data
INSERT IGNORE INTO course_student (course_id, student_id)
SELECT DISTINCT course_id, student_id FROM enrollment
WHERE course_id IS NOT NULL AND student_id IS NOT NULL;

-- 5) Add UNIQUE(student_id, subject_id) on enrollment if not exists (block duplicate enrollments)
SET @uk_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE table_schema = DATABASE() AND table_name = 'enrollment'
    AND index_name = 'uk_enrollment_student_subject'
);
SET @stmt_uk := IF(@uk_exists = 0,
    'ALTER TABLE enrollment ADD CONSTRAINT uk_enrollment_student_subject UNIQUE (student_id, subject_id)',
    'SELECT 1');
PREPARE s_uk FROM @stmt_uk; EXECUTE s_uk; DEALLOCATE PREPARE s_uk;

-- 6) Add composite FK: enrollment(course_id, subject_id) -> course_subject(course_id, subject_id)
SET @fk_cs := (
    SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'enrollment'
    AND CONSTRAINT_NAME = 'fk_enrollment_course_subject'
    LIMIT 1
);
SET @stmt_fk_cs := IF(@fk_cs IS NULL,
    'ALTER TABLE enrollment ADD CONSTRAINT fk_enrollment_course_subject FOREIGN KEY (course_id, subject_id) REFERENCES course_subject(course_id, subject_id) ON DELETE CASCADE',
    'SELECT 1');
PREPARE s_fk_cs FROM @stmt_fk_cs; EXECUTE s_fk_cs; DEALLOCATE PREPARE s_fk_cs;

-- 7) Add composite FK: enrollment(course_id, student_id) -> course_student(course_id, student_id)
SET @fk_cst := (
    SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'enrollment'
    AND CONSTRAINT_NAME = 'fk_enrollment_course_student'
    LIMIT 1
);
SET @stmt_fk_cst := IF(@fk_cst IS NULL,
    'ALTER TABLE enrollment ADD CONSTRAINT fk_enrollment_course_student FOREIGN KEY (course_id, student_id) REFERENCES course_student(course_id, student_id) ON DELETE CASCADE',
    'SELECT 1');
PREPARE s_fk_cst FROM @stmt_fk_cst; EXECUTE s_fk_cst; DEALLOCATE PREPARE s_fk_cst;
