package StudentManagementSystem.service;

import StudentManagementSystem.dto.CourseCompletionDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.CourseCompletion;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.CourseCompletionRepository;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseCompletionService {

    private final CourseCompletionRepository completionRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;

    @Transactional
    public CourseCompletionDTO recordCompletion(CourseCompletionDTO dto) {
        if (dto.getStudentId() == null || dto.getCourseId() == null) {
            throw new IllegalArgumentException("studentId and courseId are required.");
        }
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!courseStudentRepository.existsByCourse_IdAndStudent_Id(course.getId(), student.getId())) {
            throw new IllegalArgumentException("Student is not enrolled in this course.");
        }

        CourseCompletion completion = completionRepository
                .findByStudent_IdAndCourse_Id(student.getId(), course.getId())
                .orElse(new CourseCompletion());
        completion.setStudent(student);
        completion.setCourse(course);
        completion.setCompletionDate(dto.getCompletionDate() != null ? dto.getCompletionDate() : LocalDate.now());
        completion.setFinalGrade(dto.getFinalGrade());

        String certificateUrl = dto.getCertificateUrl();
        if ((certificateUrl == null || certificateUrl.isBlank()) && isPassed(dto.getFinalGrade())) {
            certificateUrl = "/certificates/course-" + course.getId() + "/student-" + student.getId() + ".pdf";
        }
        completion.setCertificateUrl(certificateUrl);
        return toDto(completionRepository.save(completion));
    }

    @Transactional(readOnly = true)
    public List<CourseCompletionDTO> getByStudent(Long studentId) {
        return completionRepository.findByStudent_Id(studentId).stream()
                .map(this::toDto)
                .toList();
    }

    private boolean isPassed(String finalGrade) {
        if (finalGrade == null || finalGrade.isBlank()) {
            return false;
        }
        try {
            return Double.parseDouble(finalGrade) >= 50.0;
        } catch (NumberFormatException ex) {
            String g = finalGrade.trim().toUpperCase();
            return g.equals("A") || g.equals("A+") || g.equals("B") || g.equals("B+") || g.equals("C");
        }
    }

    private CourseCompletionDTO toDto(CourseCompletion completion) {
        return new CourseCompletionDTO(
                completion.getId(),
                completion.getStudent() != null ? completion.getStudent().getId() : null,
                completion.getStudent() != null ? completion.getStudent().getName() : null,
                completion.getCourse() != null ? completion.getCourse().getId() : null,
                completion.getCourse() != null ? completion.getCourse().getName() : null,
                completion.getCompletionDate(),
                completion.getFinalGrade(),
                completion.getCertificateUrl()
        );
    }
}

