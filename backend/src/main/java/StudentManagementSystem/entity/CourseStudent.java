package StudentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Explicit join entity: which students are enrolled in which courses.
 * Replaces implicit ManyToMany. Enforces UNIQUE(course_id, student_id) at DB level.
 */
@Entity
@Table(name = "course_student", uniqueConstraints = {
    @UniqueConstraint(name = "uk_course_student_course_student", columnNames = {"course_id", "student_id"})
}, indexes = {
    @Index(name = "idx_course_student_course_id", columnList = "course_id"),
    @Index(name = "idx_course_student_student_id", columnList = "student_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_student_course"))
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_student_student"))
    private Student student;
}
