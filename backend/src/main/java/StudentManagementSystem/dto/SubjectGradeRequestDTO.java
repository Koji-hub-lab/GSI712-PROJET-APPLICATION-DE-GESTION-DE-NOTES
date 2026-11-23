package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectGradeRequestDTO {
    private Long courseId;
    private Long teacherId;
    private Double totalScore;
    private String grade;
}
