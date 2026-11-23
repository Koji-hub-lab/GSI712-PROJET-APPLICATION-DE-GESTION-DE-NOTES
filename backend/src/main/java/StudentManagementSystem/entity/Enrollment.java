package StudentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StudentSubject (Enrollment): student enrolled in a subject.
 * CRITICAL: Valid only when (course_id, subject_id) exists in CourseSubject
 * AND (course_id, student_id) exists in CourseStudent (same course).
 * Composite FKs are applied via Flyway migration for DB-level integrity.
 */
@Entity
@Table(name = "enrollment", uniqueConstraints = {
    @UniqueConstraint(name = "uk_enrollment_student_subject", columnNames = {"student_id", "subject_id"})
}, indexes = {
    @Index(name = "idx_enrollment_course_id", columnList = "course_id"),
    @Index(name = "idx_enrollment_student_id", columnList = "student_id"),
    @Index(name = "idx_enrollment_subject_id", columnList = "subject_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_course"))
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_subject"))
    private Subject subject;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_student"))
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;
}
