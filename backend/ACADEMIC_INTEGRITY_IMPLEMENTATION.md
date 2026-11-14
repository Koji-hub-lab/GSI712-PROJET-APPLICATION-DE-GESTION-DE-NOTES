# Academic Integrity – Database-Level Constraints (Implementation Summary)

## Phase 1: Relationship Restructure

### New join entities

**1) CourseSubject** (`entity/CourseSubject.java`)

- `id` (PK)
- `course_id` (FK → course, NOT NULL, ON DELETE CASCADE)
- `subject_id` (FK → subject, NOT NULL, ON DELETE CASCADE)
- **UNIQUE(course_id, subject_id)** — no duplicate subject per course
- Indexes on `course_id` and `subject_id`

**2) CourseStudent** (`entity/CourseStudent.java`)

- `id` (PK)
- `course_id` (FK → course, NOT NULL, ON DELETE CASCADE)
- `student_id` (FK → student, NOT NULL, ON DELETE CASCADE)
- **UNIQUE(course_id, student_id)** — no duplicate enrollment in same course
- Indexes on `course_id` and `student_id`

**3) StudentSubject (Enrollment)** (`entity/Enrollment.java`)

- `id` (PK)
- `course_id` (FK → course, NOT NULL)
- `subject_id` (FK → subject, NOT NULL)
- `student_id` (FK → student, NOT NULL)
- `status` (NOT NULL)
- **UNIQUE(student_id, subject_id)** — no duplicate enrollment in same subject
- Indexes on `course_id`, `student_id`, `subject_id`

### Refactored entities

- **Subject**: removed direct `@ManyToOne Course`. Course–subject link is only via **CourseSubject**; `Subject` has `List<CourseSubject> courseSubjects`.
- **Course**: added `List<CourseSubject> courseSubjects` and `List<CourseStudent> courseStudents` with cascade/orphanRemoval where appropriate; `teacher_id` FK and `name` NOT NULL.
- **Student**: added `List<CourseStudent> courseStudents`; `name` NOT NULL, FK to `users` named.

---

## Phase 2: Critical database rule (Option A)

**Rule:** An enrollment (StudentSubject) is valid only if:

1. The subject belongs to the course: **(course_id, subject_id)** exists in **CourseSubject**.
2. The student is in that same course: **(course_id, student_id)** exists in **CourseStudent**.

**Implementation:**

- **Enrollment** has `course_id`, `subject_id`, `student_id`.
- Flyway migration **V9** adds two composite foreign keys on `enrollment`:
  - **fk_enrollment_course_subject**: `(course_id, subject_id)` → `course_subject(course_id, subject_id)`
  - **fk_enrollment_course_student**: `(course_id, student_id)` → `course_student(course_id, student_id)`
- So the DB rejects any insert/update into `enrollment` whose `(course_id, subject_id)` or `(course_id, student_id)` is not present in the corresponding join table.

Result: a student can only be enrolled in a subject if that subject is in the course and the student is in the same course.

---

## Phase 3: SQL constraints (from Flyway V9 + JPA)

### Tables and constraints

**course_subject**

- PK: `id`
- UNIQUE: `uk_course_subject_course_subject` on `(course_id, subject_id)`
- FK: `fk_course_subject_course` → `course(id)` ON DELETE CASCADE
- FK: `fk_course_subject_subject` → `subject(id)` ON DELETE CASCADE
- Indexes: `idx_course_subject_course_id`, `idx_course_subject_subject_id`

**course_student**

- PK: `id`
- UNIQUE: `uk_course_student_course_student` on `(course_id, student_id)`
- FK: `fk_course_student_course` → `course(id)` ON DELETE CASCADE
- FK: `fk_course_student_student` → `student(id)` ON DELETE CASCADE
- Indexes: `idx_course_student_course_id`, `idx_course_student_student_id`

**enrollment**

- PK: `id`
- UNIQUE: `uk_enrollment_student_subject` on `(student_id, subject_id)` — blocks duplicate enrollments
- FK: `fk_enrollment_course` → `course(id)`
- FK: `fk_enrollment_subject` → `subject(id)`
- FK: `fk_enrollment_student` → `student(id)`
- FK: `fk_enrollment_course_subject` → `course_subject(course_id, subject_id)` ON DELETE CASCADE
- FK: `fk_enrollment_course_student` → `course_student(course_id, student_id)` ON DELETE CASCADE
- Indexes: `idx_enrollment_course_id`, `idx_enrollment_student_id`, `idx_enrollment_subject_id`

**course**

- `teacher_id` NOT NULL, FK to `teacher` (named in entity)
- `name` NOT NULL

---

## Phase 4: Service-layer validation

- **EnrollmentService.addEnrollment**
  - Requires `studentId`, `courseId`, `subjectId`.
  - Checks **CourseSubject** exists for `(courseId, subjectId)`; otherwise throws: *"Subject does not belong to this course..."*.
  - Checks **CourseStudent** exists for `(courseId, studentId)`; otherwise throws: *"Student is not enrolled in this course..."*.
  - Checks no existing enrollment for `(studentId, subjectId)`; otherwise throws *"Student is already enrolled in this subject."*.
  - Catches **DataIntegrityViolationException** and maps to clear messages (duplicate, invalid course_subject, invalid course_student).
- **CourseService**
  - **addSubjectToCourse** / **addStudentToCourse**: validate and persist; catch **DataIntegrityViolationException** and rethrow with a clear message.
  - **ensureTeacherOwnsCourse(courseId, teacherId)**: throws **AccessDeniedException** if the course is not assigned to that teacher.
- **GradeService.updateGrade**
  - Optional **teacherId**: when present, calls **ensureTeacherOwnsCourse** so a teacher cannot change grades for a course not assigned to them.
- **GlobalExceptionHandler**
  - **DataIntegrityViolationException** → 409 with constraint message.
  - **AccessDeniedException** → 403.
  - **IllegalArgumentException** → 400.

---

## Phase 5: Confirmations

| Requirement | How it is enforced |
|------------|---------------------|
| **Student can only enroll in subjects that belong to courses they are enrolled in** | Composite FK `enrollment(course_id, subject_id)` → `course_subject` and `enrollment(course_id, student_id)` → `course_student`; both must hold. Service also checks before insert. |
| **Duplicate enrollments impossible at DB level** | UNIQUE constraint `uk_enrollment_student_subject` on `(student_id, subject_id)`. Any second enrollment for same student+subject is rejected by the DB. |
| **Teacher can only manage courses assigned to them** | Course has `teacher_id` FK. **CourseService.ensureTeacherOwnsCourse(courseId, teacherId)** used in grade update; can be used in any teacher-scoped API. DB does not allow course without a valid teacher. |
| **Invalid relationships blocked even if backend logic fails** | Composite FKs on `enrollment` ensure: (1) subject is in the course, (2) student is in the course. Duplicate enrollment is blocked by UNIQUE. So invalid or duplicate inserts are rejected by the database. |

---

## API usage (enrollment flow)

1. **Assign subject to course**  
   `POST /course/{courseId}/subjects/{subjectId}`  
   (or admin: `POST /admin/courses/{courseId}/subjects` with body `{"subjectIds": [...]}`)

2. **Enroll student in course**  
   `POST /course/{courseId}/students/{studentId}`

3. **Enroll student in subject (within that course)**  
   `POST /enrollment/add` with body including `studentId`, `courseId`, `subjectId`, and optional `status`.

If (1) or (2) is skipped, step (3) fails in service validation or with a DB constraint violation.

---

## Files touched

- **New:** `entity/CourseSubject.java`, `entity/CourseStudent.java`, `repository/CourseSubjectRepository.java`, `repository/CourseStudentRepository.java`, `db/migration/V9__academic_integrity_join_tables_and_fks.sql`
- **Updated:** `entity/Course.java`, `entity/Subject.java`, `entity/Student.java`, `entity/Enrollment.java`, `service/EnrollmentService.java`, `service/CourseService.java`, `service/GradeService.java`, `controller/CourseController.java`, `controller/TeacherPortalController.java`, `controller/EnrollmentController.java`, `controller/AdminController.java`, `controller/SubjectController.java`, `exception/GlobalExceptionHandler.java` (already handled DataIntegrityViolationException)

This gives an enterprise-grade, integrity-safe schema and service layer.
