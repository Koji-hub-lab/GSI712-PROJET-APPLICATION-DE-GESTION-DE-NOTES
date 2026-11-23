package StudentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_subject_grade", uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_subject_grade_student_subject",
                columnNames = {"student_id", "subject_id"})
}, indexes = {
        @Index(name = "idx_ssg_student_id", columnList = "student_id"),
        @Index(name = "idx_ssg_subject_id", columnList = "subject_id"),
        @Index(name = "idx_ssg_course_id", columnList = "course_id"),
        @Index(name = "idx_ssg_graded_by", columnList = "graded_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ssg_student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ssg_subject"))
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ssg_course"))
    private Course course;

    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @Column(nullable = false, length = 2)
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by", foreignKey = @ForeignKey(name = "fk_ssg_teacher"))
    private Teacher gradedBy;

    @Column(name = "graded_at", nullable = false)
    private LocalDateTime gradedAt;
}
