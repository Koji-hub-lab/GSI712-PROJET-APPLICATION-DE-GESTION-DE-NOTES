package StudentManagementSystem.controller;

import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.StudentCourseDTO;
import StudentManagementSystem.dto.SubjectLiteDTO;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.service.CourseService;
import StudentManagementSystem.service.StudentPortalService;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentPortalController {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final StudentPortalService studentPortalService;
    private final CourseService courseService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats(@RequestParam(required = false) Long studentId) {
        Map<String, Object> stats = new HashMap<>();
        if (studentId != null) {
            long courses = courseStudentRepository.findByStudent_Id(studentId).size();
            long grades = enrollmentRepository.countByStudent_Id(studentId);
            stats.put("totalCourses", courses);
            stats.put("totalGrades", grades);
        } else {
            // Fallback to legacy aggregated numbers
            stats.put("totalCourses", enrollmentRepository.count());
            stats.put("totalGrades", enrollmentRepository.count());
        }
        return ApiResponse.ok(stats);
    }

    @GetMapping("/courses")
    public ApiResponse<List<StudentCourseDTO>> courses(@RequestParam(required = false) Long studentId) {
        return ApiResponse.ok(studentPortalService.getCourses(studentId));
    }

    /** Subjects in a course; optionally enforce that the requesting student is enrolled. */
    @GetMapping("/courses/{courseId}/subjects")
    public ApiResponse<List<SubjectLiteDTO>> subjectsInCourse(@RequestParam(required = false) Long studentId,
                                                              @org.springframework.web.bind.annotation.PathVariable Long courseId) {
        if (studentId != null && !courseStudentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId)) {
            throw new org.springframework.security.access.AccessDeniedException("Student is not enrolled in this course");
        }
        List<Subject> subjects = courseService.getSubjectsInCourse(courseId);
        List<SubjectLiteDTO> list = subjects.stream()
                .map(s -> new SubjectLiteDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());
        return ApiResponse.ok(list);
    }
}
