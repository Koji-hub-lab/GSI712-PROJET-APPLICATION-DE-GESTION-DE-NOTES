package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseDTO {
    private Long id;
    private String name;
    private String teacherName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<SubjectLiteDTO> subjects = new ArrayList<>();
    private List<SubjectLiteDTO> enrolledSubjects = new ArrayList<>();
}

