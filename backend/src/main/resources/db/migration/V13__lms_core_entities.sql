-- LMS core extension tables

CREATE TABLE IF NOT EXISTS semester (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    INDEX idx_semester_status (status),
    INDEX idx_semester_start_end (start_date, end_date)
);

SET @course_semester_col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'course' AND COLUMN_NAME = 'semester_id'
);
SET @stmt_course_semester_col := IF(
    @course_semester_col_exists = 0,
    'ALTER TABLE course ADD COLUMN semester_id BIGINT NULL',
    'SELECT 1'
);
PREPARE s_course_semester_col FROM @stmt_course_semester_col; EXECUTE s_course_semester_col; DEALLOCATE PREPARE s_course_semester_col;

SET @course_semester_fk_exists := (
    SELECT COUNT(*) FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'course' AND CONSTRAINT_NAME = 'fk_course_semester'
);
SET @stmt_course_semester_fk := IF(
    @course_semester_fk_exists = 0,
    'ALTER TABLE course ADD CONSTRAINT fk_course_semester FOREIGN KEY (semester_id) REFERENCES semester(id)',
    'SELECT 1'
);
PREPARE s_course_semester_fk FROM @stmt_course_semester_fk; EXECUTE s_course_semester_fk; DEALLOCATE PREPARE s_course_semester_fk;

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    recorded_by BIGINT NULL,
    CONSTRAINT uk_attendance_student_subject_date UNIQUE (student_id, subject_id, attendance_date),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_attendance_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_attendance_course FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT fk_attendance_teacher FOREIGN KEY (recorded_by) REFERENCES teacher(id),
    INDEX idx_attendance_course_id (course_id),
    INDEX idx_attendance_student_id (student_id),
    INDEX idx_attendance_subject_id (subject_id),
    INDEX idx_attendance_date (attendance_date)
);

CREATE TABLE IF NOT EXISTS assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    due_date DATETIME NOT NULL,
    max_score DOUBLE NOT NULL,
    created_by BIGINT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_assignment_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_assignment_course FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT fk_assignment_teacher FOREIGN KEY (created_by) REFERENCES teacher(id),
    INDEX idx_assignment_course_id (course_id),
    INDEX idx_assignment_subject_id (subject_id),
    INDEX idx_assignment_due_date (due_date)
);

CREATE TABLE IF NOT EXISTS assignment_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    submitted_at DATETIME NOT NULL,
    score DOUBLE NULL,
    feedback VARCHAR(2000) NULL,
    CONSTRAINT uk_submission_assignment_student UNIQUE (assignment_id, student_id),
    CONSTRAINT fk_submission_assignment FOREIGN KEY (assignment_id) REFERENCES assignment(id),
    CONSTRAINT fk_submission_student FOREIGN KEY (student_id) REFERENCES student(id),
    INDEX idx_submission_assignment_id (assignment_id),
    INDEX idx_submission_student_id (student_id)
);

CREATE TABLE IF NOT EXISTS course_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    uploaded_by BIGINT NULL,
    uploaded_at DATETIME NOT NULL,
    CONSTRAINT fk_material_course FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT fk_material_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_material_teacher FOREIGN KEY (uploaded_by) REFERENCES teacher(id),
    INDEX idx_material_course_id (course_id),
    INDEX idx_material_subject_id (subject_id),
    INDEX idx_material_uploaded_at (uploaded_at)
);

CREATE TABLE IF NOT EXISTS class_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    classroom VARCHAR(255) NOT NULL,
    CONSTRAINT fk_schedule_course FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT fk_schedule_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_schedule_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    INDEX idx_schedule_course_id (course_id),
    INDEX idx_schedule_teacher_id (teacher_id)
);

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    type VARCHAR(40) NOT NULL,
    created_at DATETIME NOT NULL,
    is_read BIT NOT NULL DEFAULT 0,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_notification_user_id (user_id),
    INDEX idx_notification_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS course_completion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    completion_date DATE NOT NULL,
    final_grade VARCHAR(50),
    certificate_url VARCHAR(1000),
    CONSTRAINT uk_completion_student_course UNIQUE (student_id, course_id),
    CONSTRAINT fk_completion_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_completion_course FOREIGN KEY (course_id) REFERENCES course(id),
    INDEX idx_completion_student_id (student_id),
    INDEX idx_completion_course_id (course_id)
);

