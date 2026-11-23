package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectGradeDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Long courseId;
    private String courseName;
    private Double totalScore;
    private String grade;
    private Long gradedById;
    private String gradedByName;
    private LocalDateTime gradedAt;
}
