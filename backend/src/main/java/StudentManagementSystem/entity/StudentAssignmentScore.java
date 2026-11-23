package StudentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_assignment_score", uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_assignment_score_student_assignment",
                columnNames = {"student_id", "assignment_id"})
}, indexes = {
        @Index(name = "idx_sas_student_id", columnList = "student_id"),
        @Index(name = "idx_sas_assignment_id", columnList = "assignment_id"),
        @Index(name = "idx_sas_graded_by", columnList = "graded_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignmentScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sas_student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sas_assignment"))
    private Assignment assignment;

    @Column(nullable = false)
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by", foreignKey = @ForeignKey(name = "fk_sas_teacher"))
    private Teacher gradedBy;

    @Column(name = "graded_at", nullable = false)
    private LocalDateTime gradedAt;

    @Column(length = 2000)
    private String feedback;
}
