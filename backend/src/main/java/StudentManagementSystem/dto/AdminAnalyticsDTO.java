package StudentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsDTO {
    private long totalStudents;
    private long totalCourses;
    private long totalEnrollments;
    private double completionRate;
}

