package StudentManagementSystem.service;

import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.CourseStudent;
import StudentManagementSystem.entity.CourseSubject;
import StudentManagementSystem.entity.Enrollment;
import StudentManagementSystem.entity.EnrollmentStatus;
import StudentManagementSystem.entity.NotificationType;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.EnrollmentRepository;
import StudentManagementSystem.repository.StudentRepository;
import StudentManagementSystem.repository.SubjectRepository;
import StudentManagementSystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NotificationService notificationService;
    private volatile boolean enrollmentStatusChecked;

    /**
     * Enroll a student in a subject. Enforced at DB level and validated here:
     * - Subject must belong to the given course (CourseSubject).
     * - Student must be enrolled in the given course (CourseStudent).
     * - No duplicate (student_id, subject_id).
     */
    public Enrollment addEnrollment(Enrollment enrollment, Long studentId, Long courseId, Long subjectId) {
        if (courseId == null || subjectId == null || studentId == null) {
            throw new IllegalArgumentException("studentId, courseId and subjectId are required");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        // 1) Student can ONLY enroll in subjects that belong to courses they are enrolled in
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(courseId, subjectId)) {
            throw new IllegalArgumentException(
                "Subject does not belong to this course. Add the subject to the course first (CourseSubject).");
        }
        if (!courseStudentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId)) {
            throw new IllegalArgumentException(
                "Student is not enrolled in this course. Enroll the student in the course first (CourseStudent).");
        }

        // 2) Duplicate enrollment check (also enforced by DB UNIQUE(student_id, subject_id))
        if (enrollmentRepository.existsByStudent_IdAndSubject_Id(studentId, subjectId)) {
            throw new IllegalStateException("Student is already enrolled in this subject.");
        }

        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        enrollment.setCourse(course);
        if (enrollment.getStatus() == null) {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        }

        ensureEnrollmentStatusColumnCompatibleOnce();

        try {
            return saveAndNotify(enrollment, student, course, subject);
        } catch (RuntimeException e) {
            String msg = mostSpecificMessage(e);

            if (isStatusTruncation(msg)) {
                log.warn("Detected legacy enrollment.status schema. Applying hotfix and retrying insert.");
                fixEnrollmentStatusColumnDefinition();
                try {
                    return saveAndNotify(enrollment, student, course, subject);
                } catch (RuntimeException retry) {
                    msg = mostSpecificMessage(retry);
                }
            }

            if (msg != null && (msg.contains("uk_enrollment_student_subject") || msg.contains("Duplicate"))) {
                throw new IllegalStateException("Student is already enrolled in this subject.", e);
            }
            if (msg != null && (msg.contains("fk_enrollment_course_subject") || msg.contains("course_subject"))) {
                throw new IllegalStateException("Subject is not part of this course. Invalid enrollment.", e);
            }
            if (msg != null && (msg.contains("fk_enrollment_course_student") || msg.contains("course_student"))) {
                throw new IllegalStateException("Student is not enrolled in this course. Invalid enrollment.", e);
            }
            if (isStatusTruncation(msg)) {
                throw new IllegalStateException(
                        "Enrollment schema mismatch on status column. Run database migration to set enrollment.status to VARCHAR(20).",
                        e
                );
            }
            throw new IllegalStateException("Database rejected the enrollment: " + msg, e);
        }
    }

    private Enrollment saveAndNotify(Enrollment enrollment, Student student, Course course, Subject subject) {
        Enrollment saved = enrollmentRepository.save(enrollment);
        if (student.getUser() != null && student.getUser().getId() != null) {
            try {
                notificationService.createForUser(
                        student.getUser().getId(),
                        "Course Enrollment Updated",
                        "You were enrolled in subject '" + subject.getName() + "' for course '" + course.getName() + "'.",
                        NotificationType.COURSE_ENROLLMENT
                );
            } catch (RuntimeException notifyEx) {
                log.warn("Enrollment created, but notification dispatch failed: {}", mostSpecificMessage(notifyEx));
            }
        }
        return saved;
    }

    private String mostSpecificMessage(Throwable e) {
        if (e == null) {
            return null;
        }
        if (e instanceof DataIntegrityViolationException dive && dive.getMostSpecificCause() != null) {
            return dive.getMostSpecificCause().getMessage();
        }
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root.getMessage() != null ? root.getMessage() : e.getMessage();
    }

    private boolean isStatusTruncation(String message) {
        if (message == null) return false;
        String normalized = message.toLowerCase();
        return normalized.contains("data truncated for column 'status'")
                || normalized.contains("data truncated for column \"status\"")
                || (normalized.contains("data truncated") && normalized.contains("status"));
    }

    private void fixEnrollmentStatusColumnDefinition() {
        jdbcTemplate.execute("ALTER TABLE enrollment MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED'");
        jdbcTemplate.execute(
                "UPDATE enrollment SET status = CASE " +
                        "WHEN status IS NULL OR TRIM(status) = '' OR status = '0' THEN 'ENROLLED' " +
                        "WHEN status = '1' THEN 'COMPLETED' " +
                        "WHEN status = '2' THEN 'DROPPED' " +
                        "ELSE status END"
        );
    }

    private void ensureEnrollmentStatusColumnCompatibleOnce() {
        if (enrollmentStatusChecked) {
            return;
        }
        synchronized (this) {
            if (enrollmentStatusChecked) {
                return;
            }
            try {
                java.util.Map<String, Object> column = jdbcTemplate.query(
                        "SELECT DATA_TYPE AS dataType, CHARACTER_MAXIMUM_LENGTH AS maxLen FROM information_schema.COLUMNS " +
                                "WHERE TABLE_SCHEMA = DATABASE() " +
                                "AND TABLE_NAME = 'enrollment' " +
                                "AND COLUMN_NAME = 'status'",
                        rs -> rs.next()
                                ? java.util.Map.of(
                                "dataType", rs.getString("dataType"),
                                "maxLen", rs.getObject("maxLen"))
                                : null
                );
                if (column != null) {
                    String dataType = String.valueOf(column.get("dataType"));
                    Object maxLenObj = column.get("maxLen");
                    Long maxLen = maxLenObj instanceof Number ? ((Number) maxLenObj).longValue() : null;
                    boolean needsAlter = !"varchar".equalsIgnoreCase(dataType) || maxLen == null || maxLen < 20;
                    if (needsAlter) {
                        log.warn("Pre-check detected incompatible enrollment.status type={}, maxLen={}. Applying compatibility fix.",
                                dataType, maxLen);
                        fixEnrollmentStatusColumnDefinition();
                    }
                }
            } catch (Exception e) {
                log.warn("Could not pre-check enrollment.status schema: {}", e.getMessage());
            } finally {
                enrollmentStatusChecked = true;
            }
        }
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
    }

    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrollment not found");
        }
        enrollmentRepository.deleteById(id);
    }
}
