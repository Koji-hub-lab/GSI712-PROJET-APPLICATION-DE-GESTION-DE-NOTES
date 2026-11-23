package StudentManagementSystem.controller;

import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.CourseDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.CourseStudent;
import StudentManagementSystem.entity.CourseSubject;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final ModelMapper mapper;

    @GetMapping("/all")
    public ApiResponse<List<CourseDTO>> getAllCourses() {
        return ApiResponse.ok(courseService.getAllCourses()
                .stream().map(this::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/add")
    public ApiResponse<CourseDTO> addCourse(@RequestBody CourseDTO courseDTO) {
        Course course = mapper.map(courseDTO, Course.class);
        validateDates(courseDTO);
        return ApiResponse.ok(toDto(courseService.addCourse(course)));
    }

    /** List courses the student is enrolled in (CourseStudent). */
    @GetMapping("/student/{studentId}/courses")
    public ApiResponse<List<CourseDTO>> getCoursesForStudent(@PathVariable Long studentId) {
        List<Course> courses = courseService.getCoursesByStudentId(studentId);
        return ApiResponse.ok(courses.stream().map(this::toDto).collect(Collectors.toList()));
    }

    /** List subjects assigned to this course (CourseSubject). */
    @GetMapping("/{courseId}/subjects")
    public ApiResponse<List<Map<String, Object>>> getSubjectsInCourse(@PathVariable Long courseId) {
        List<Subject> subjects = courseService.getSubjectsInCourse(courseId);
        List<Map<String, Object>> list = subjects.stream()
                .map(s -> Map.<String, Object>of("id", s.getId(), "name", s.getName()))
                .collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    /** Assign a subject to a course (required before students can enroll in that subject in this course). */
    @PostMapping("/{courseId}/subjects/{subjectId}")
    public ApiResponse<?> addSubjectToCourse(@PathVariable Long courseId, @PathVariable Long subjectId) {
        CourseSubject cs = courseService.addSubjectToCourse(courseId, subjectId);
        return ApiResponse.ok(Map.of("id", cs.getId(), "courseId", courseId, "subjectId", subjectId));
    }

    /** Enroll a student in a course (required before student can enroll in subjects of this course). */
    @PostMapping("/{courseId}/students/{studentId}")
    public ApiResponse<?> addStudentToCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        CourseStudent cs = courseService.addStudentToCourse(courseId, studentId);
        return ApiResponse.ok(Map.of("id", cs.getId(), "courseId", courseId, "studentId", studentId));
    }

    private CourseDTO toDto(Course c) {
        CourseDTO dto = mapper.map(c, CourseDTO.class);
        if (c.getTeacher() != null) {
            dto.setTeacherId(c.getTeacher().getId());
            dto.setTeacherName(c.getTeacher().getName());
        }
        if (c.getSemester() != null) {
            dto.setSemesterId(c.getSemester().getId());
            dto.setSemesterName(c.getSemester().getName());
        }
        if (c.getCourseStudents() != null) {
            dto.setEnrolledStudentIds(
                    c.getCourseStudents().stream()
                            .map(cs -> cs.getStudent() != null ? cs.getStudent().getId() : null)
                            .filter(id -> id != null)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    private void validateDates(CourseDTO dto) {
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new IllegalArgumentException("endDate must be on or after startDate");
            }
        }
    }
}
