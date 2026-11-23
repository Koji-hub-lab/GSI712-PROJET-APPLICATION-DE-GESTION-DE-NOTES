package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long courseId;
    private String courseName;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Double maxScore;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}

