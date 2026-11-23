package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentScoreRequestDTO {
    private Long studentId;
    private Long teacherId;
    private Double score;
    private String feedback;
}
