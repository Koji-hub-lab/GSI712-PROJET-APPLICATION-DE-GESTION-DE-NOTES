package StudentManagementSystem.dto;


import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String name;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private Long teacherId;
    private String teacherName;
    private Long semesterId;
    private String semesterName;
    private List<Long> enrolledStudentIds;
}
