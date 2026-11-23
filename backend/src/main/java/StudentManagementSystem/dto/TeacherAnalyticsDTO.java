package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAnalyticsDTO {
    private long managedCourses;
    private long managedStudents;
    private double averageScore;
    private double attendancePercentage;
    private double assignmentCompletionRate;
}

