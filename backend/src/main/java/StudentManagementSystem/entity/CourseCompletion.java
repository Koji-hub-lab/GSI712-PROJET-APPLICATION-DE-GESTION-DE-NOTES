package StudentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "course_completion", uniqueConstraints = {
        @UniqueConstraint(name = "uk_completion_student_course", columnNames = {"student_id", "course_id"})
}, indexes = {
        @Index(name = "idx_completion_student_id", columnList = "student_id"),
        @Index(name = "idx_completion_course_id", columnList = "course_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_completion_student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_completion_course"))
    private Course course;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    @Column(name = "final_grade")
    private String finalGrade;

    @Column(name = "certificate_url", length = 1000)
    private String certificateUrl;
}

