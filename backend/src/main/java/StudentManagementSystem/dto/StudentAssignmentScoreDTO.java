package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignmentScoreDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String courseName;
    private Long subjectId;
    private String subjectName;
    private Double score;
    private Double maxScore;
    private Long gradedById;
    private String gradedByName;
    private LocalDateTime gradedAt;
    private String feedback;
}
