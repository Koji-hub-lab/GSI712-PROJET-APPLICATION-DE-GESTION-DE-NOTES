package StudentManagementSystem.service;

import StudentManagementSystem.entity.Grade;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final CourseService courseService;

    public Grade addGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
    }

    /** Update grade; if teacherId is provided, ensures the grade's course is assigned to that teacher. */
    public Grade updateGrade(Long gradeId, String gradeValue, Long teacherId) {
        Grade grade = getGradeById(gradeId);
        if (grade.getEnrollment() != null && grade.getEnrollment().getCourse() != null && teacherId != null) {
            courseService.ensureTeacherOwnsCourse(grade.getEnrollment().getCourse().getId(), teacherId);
        }
        grade.setGrade(gradeValue);
        return gradeRepository.save(grade);
    }

    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }
}
