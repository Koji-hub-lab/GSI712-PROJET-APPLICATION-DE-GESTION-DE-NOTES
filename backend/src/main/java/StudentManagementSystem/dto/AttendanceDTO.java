package StudentManagementSystem.dto;

import StudentManagementSystem.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Long courseId;
    private String courseName;
    private LocalDate date;
    private AttendanceStatus status;
    private Long recordedById;
    private String recordedByName;
}

