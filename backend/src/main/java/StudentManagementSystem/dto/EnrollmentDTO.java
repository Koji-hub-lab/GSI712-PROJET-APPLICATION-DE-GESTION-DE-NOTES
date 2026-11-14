package StudentManagementSystem.dto;

import StudentManagementSystem.entity.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private String studentName;
    private Long studentId;
    private String courseName;
    private Long courseId;
    private Long subjectId;
    private String subjectName;
    private EnrollmentStatus status;
}
