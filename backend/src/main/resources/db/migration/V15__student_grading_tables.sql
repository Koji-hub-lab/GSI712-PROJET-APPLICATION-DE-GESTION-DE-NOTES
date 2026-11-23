-- Student grading tables:
-- 1) per-assignment scores
-- 2) final per-subject grades

CREATE TABLE IF NOT EXISTS student_assignment_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    assignment_id BIGINT NOT NULL,
    score DOUBLE NOT NULL,
    graded_by BIGINT NULL,
    graded_at DATETIME NOT NULL,
    feedback VARCHAR(2000) NULL,
    CONSTRAINT uk_student_assignment_score_student_assignment UNIQUE (student_id, assignment_id),
    CONSTRAINT fk_sas_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_sas_assignment FOREIGN KEY (assignment_id) REFERENCES assignment(id),
    CONSTRAINT fk_sas_teacher FOREIGN KEY (graded_by) REFERENCES teacher(id),
    INDEX idx_sas_student_id (student_id),
    INDEX idx_sas_assignment_id (assignment_id),
    INDEX idx_sas_graded_by (graded_by)
);

CREATE TABLE IF NOT EXISTS student_subject_grade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    total_score DOUBLE NOT NULL,
    grade VARCHAR(2) NOT NULL,
    graded_by BIGINT NULL,
    graded_at DATETIME NOT NULL,
    CONSTRAINT uk_student_subject_grade_student_subject UNIQUE (student_id, subject_id),
    CONSTRAINT fk_ssg_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_ssg_subject FOREIGN KEY (subject_id) REFERENCES subject(id),
    CONSTRAINT fk_ssg_course FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT fk_ssg_teacher FOREIGN KEY (graded_by) REFERENCES teacher(id),
    INDEX idx_ssg_student_id (student_id),
    INDEX idx_ssg_subject_id (subject_id),
    INDEX idx_ssg_course_id (course_id),
    INDEX idx_ssg_graded_by (graded_by)
);
