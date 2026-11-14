package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseStudentDTO {
    private Long studentId;
    private String name;
    private String email;
    private List<SubjectLiteDTO> assignedSubjects = new ArrayList<>();
}

