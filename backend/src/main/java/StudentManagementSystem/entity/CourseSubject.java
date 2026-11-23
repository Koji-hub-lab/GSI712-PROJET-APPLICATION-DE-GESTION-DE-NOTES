package StudentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Explicit join entity: which subjects belong to which courses.
 * Replaces implicit ManyToMany. Enforces UNIQUE(course_id, subject_id) at DB level.
 */
@Entity
@Table(name = "course_subject", uniqueConstraints = {
    @UniqueConstraint(name = "uk_course_subject_course_subject", columnNames = {"course_id", "subject_id"})
}, indexes = {
    @Index(name = "idx_course_subject_course_id", columnList = "course_id"),
    @Index(name = "idx_course_subject_subject_id", columnList = "subject_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_subject_course"))
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_subject_subject"))
    private Subject subject;
}
