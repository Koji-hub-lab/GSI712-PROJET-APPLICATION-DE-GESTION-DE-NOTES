package StudentManagementSystem.controller;

import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.AssignSubjectsRequestDTO;
import StudentManagementSystem.dto.TeacherCourseAssignmentsDTO;
import StudentManagementSystem.dto.TeacherCourseStudentDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.Enrollment;
import StudentManagementSystem.entity.Grade;
import StudentManagementSystem.entity.CourseStudent;
import StudentManagementSystem.entity.CourseSubject;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.EnrollmentRepository;
import StudentManagementSystem.repository.GradeRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.service.GradeService;
import StudentManagementSystem.service.TeacherAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherPortalController {

    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final GradeService gradeService;
    private final TeacherAssignmentService teacherAssignmentService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", enrollmentRepository.count());
        stats.put("totalCourses", courseRepository.count());
        return ApiResponse.ok(stats);
    }

    @GetMapping("/courses")
    public ApiResponse<List<Map<String, Object>>> courses(@RequestParam(required = false) Long teacherId) {
        List<Course> courses = courseRepository.findAll();
        if (teacherId != null) {
            courses = courses.stream()
                    .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacherId))
                    .toList();
        }

        List<Map<String, Object>> data = courses.stream().map(c -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", c.getId());
            row.put("name", c.getName());
            row.put("studentsCount", courseStudentRepository.countByCourse_Id(c.getId()));
            row.put("subjectsCount", courseSubjectRepository.findByCourse_Id(c.getId()).size());
            row.put("startDate", "");
            return row;
        }).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/students")
    public ApiResponse<List<Map<String, Object>>> students(@RequestParam(required = false) Long teacherId) {
        List<Course> courses = courseRepository.findAll();
        if (teacherId != null) {
            courses = courses.stream()
                    .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacherId))
                    .toList();
        }
        List<Long> courseIds = courses.stream().map(Course::getId).toList();

        List<Map<String, Object>> data = courseIds.stream()
                .flatMap(id -> courseStudentRepository.findByCourse_Id(id).stream())
                .map(cs -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", cs.getStudent() != null ? cs.getStudent().getId() : null);
                    row.put("name", cs.getStudent() != null ? cs.getStudent().getName() : null);
                    row.put("email", cs.getStudent() != null ? cs.getStudent().getEmail() : null);
                    row.put("courseName", cs.getCourse() != null ? cs.getCourse().getName() : null);
                    return row;
                }).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/grades")
    public ApiResponse<List<Map<String, Object>>> grades() {
        List<Map<String, Object>> data = gradeRepository.findAll().stream().map(g -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", g.getId());
            Enrollment enrollment = g.getEnrollment();
            row.put("studentName", enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getName() : null);
            row.put("courseName", enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getName() : null);
            row.put("grade", g.getGrade());
            return row;
        }).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @PutMapping("/grades/{id}")
    public ApiResponse<Map<String, Object>> updateGrade(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String gradeValue = request.get("grade");
        // Pass teacherId from SecurityContext when available; null = skip ownership check (e.g. admin)
        Grade saved = gradeService.updateGrade(id, gradeValue, null);
        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("grade", saved.getGrade());
        return ApiResponse.ok(response);
    }

    @GetMapping("/analytics")
    public ApiResponse<Map<String, Object>> analytics() {
        List<Grade> grades = gradeRepository.findAll();
        double avg = grades.stream()
                .map(Grade::getGrade)
                .filter(Objects::nonNull)
                .mapToDouble(g -> {
                    try { return Double.parseDouble(g); } catch (NumberFormatException e) { return 0; }
                })
                .average().orElse(0);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalStudents", enrollmentRepository.count());
        summary.put("totalCourses", courseRepository.count());
        summary.put("averageGrade", avg);

        List<Map<String, Object>> byCourse = courseRepository.findAll().stream().map(course -> {
            Map<String, Object> row = new HashMap<>();
            row.put("courseId", course.getId());
            row.put("courseName", course.getName());
            int students = course.getEnrollments() != null ? course.getEnrollments().size() : 0;
            row.put("studentsCount", students);
            row.put("averageGrade", avg);
            row.put("completionRate", "-");
            return row;
        }).collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("summary", summary);
        payload.put("byCourse", byCourse);
        return ApiResponse.ok(payload);
    }

    @GetMapping("/subjects")
    public ApiResponse<List<Map<String, Object>>> subjects(@RequestParam(required = false) Long teacherId) {
        List<Course> courses = courseRepository.findAll();
        if (teacherId != null) {
            courses = courses.stream()
                    .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacherId))
                    .toList();
        }
        List<Map<String, Object>> data = courses.stream()
                .flatMap(c -> courseSubjectRepository.findByCourse_Id(c.getId()).stream())
                .map(cs -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("courseId", cs.getCourse().getId());
                    row.put("courseName", cs.getCourse().getName());
                    row.put("subjectId", cs.getSubject().getId());
                    row.put("subjectName", cs.getSubject().getName());
                    return row;
                }).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/courses/{courseId}/subject-assignments")
    public ApiResponse<TeacherCourseAssignmentsDTO> getCourseSubjectAssignments(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long teacherId
    ) {
        return ApiResponse.ok(teacherAssignmentService.getCourseAssignments(courseId, teacherId));
    }

    @PutMapping("/courses/{courseId}/students/{studentId}/subjects")
    public ApiResponse<TeacherCourseStudentDTO> assignSubjectsToStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestParam(required = false) Long teacherId,
            @RequestBody AssignSubjectsRequestDTO request
    ) {
        List<Long> subjectIds = request != null ? request.getSubjectIds() : List.of();
        return ApiResponse.ok(teacherAssignmentService.assignSubjectsToStudent(
                courseId,
                studentId,
                teacherId,
                subjectIds));
    }
}
