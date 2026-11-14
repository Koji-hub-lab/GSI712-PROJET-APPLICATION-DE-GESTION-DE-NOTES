package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseAssignmentsDTO {
    private Long courseId;
    private String courseName;
    private List<SubjectLiteDTO> subjects = new ArrayList<>();
    private List<TeacherCourseStudentDTO> students = new ArrayList<>();
}

