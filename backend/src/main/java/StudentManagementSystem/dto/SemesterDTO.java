package StudentManagementSystem.dto;

import StudentManagementSystem.entity.SemesterStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private SemesterStatus status;
}

