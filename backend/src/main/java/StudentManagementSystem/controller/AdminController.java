package StudentManagementSystem.controller;

import StudentManagementSystem.dto.*;
import StudentManagementSystem.entity.*;
import StudentManagementSystem.repository.*;
import StudentManagementSystem.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final CourseService courseService;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTeachers", teacherRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalCourses", courseRepository.count());
        return ApiResponse.ok(stats);
    }

    // Teachers
    @GetMapping("/teachers")
    public ApiResponse<List<TeacherDTO>> getTeachers() {
        List<TeacherDTO> list = teacherRepository.findAll().stream()
                .map(t -> {
                    TeacherDTO dto = mapper.map(t, TeacherDTO.class);
                    dto.setUserId(t.getUser() != null ? t.getUser().getId() : null);
                    return dto;
                })
                .collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    @PostMapping("/teachers")
    public ResponseEntity<ApiResponse<?>> addTeacher(@RequestBody TeacherDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(409).body(ApiResponse.error("Email already exists"));
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.TEACHER);
        user.setActive(true);
        userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        teacher.setUser(user);
        Teacher saved = teacherRepository.save(teacher);
        TeacherDTO dto = mapper.map(saved, TeacherDTO.class);
        dto.setUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PutMapping("/teachers/{id}")
    public ApiResponse<TeacherDTO> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO request) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        Teacher updated = teacherRepository.save(teacher);

        if (teacher.getUser() != null) {
            User user = teacher.getUser();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            userRepository.save(user);
            request.setUserId(user.getId());
        }

        TeacherDTO dto = mapper.map(updated, TeacherDTO.class);
        dto.setUserId(updated.getUser() != null ? updated.getUser().getId() : request.getUserId());
        return ApiResponse.ok(dto);
    }

    @DeleteMapping("/teachers/{id}")
    public ApiResponse<?> deleteTeacher(@PathVariable Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
        Long userId = teacher.getUser() != null ? teacher.getUser().getId() : null;
        teacherRepository.deleteById(id);
        if (userId != null) {
            userRepository.deleteById(userId);
        }
        return ApiResponse.ok(Map.of("deleted", id));
    }

    // Students
    @GetMapping("/students")
    public ApiResponse<List<StudentDTO>> getStudents() {
        List<StudentDTO> list = studentRepository.findAll().stream()
                .map(s -> mapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    @PostMapping("/students")
    public ResponseEntity<ApiResponse<?>> addStudent(@RequestBody StudentDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(409).body(ApiResponse.error("Email already exists"));
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setActive(true);
        userRepository.save(user);

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setUser(user);
        Student saved = studentRepository.save(student);

        return ResponseEntity.ok(ApiResponse.ok(mapper.map(saved, StudentDTO.class)));
    }

    @PutMapping("/students/{id}")
    public ApiResponse<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentDTO request) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        Student updated = studentRepository.save(student);

        if (student.getUser() != null) {
            User user = student.getUser();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }

        return ApiResponse.ok(mapper.map(updated, StudentDTO.class));
    }

    @DeleteMapping("/students/{id}")
    public ApiResponse<?> deleteStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        Long userId = student.getUser() != null ? student.getUser().getId() : null;
        studentRepository.deleteById(id);
        if (userId != null) {
            userRepository.deleteById(userId);
        }
        return ApiResponse.ok(Map.of("deleted", id));
    }

    // Courses
    @GetMapping({"/courses", "/subjects"})
    public ApiResponse<List<CourseDTO>> getCourses() {
        List<CourseDTO> list = courseRepository.findAll().stream()
                .map(this::toCourseDTO)
                .collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    @PostMapping({"/courses", "/subjects"})
    public ApiResponse<CourseDTO> addCourse(@RequestBody CourseDTO request) {
        validateCourseDates(request);
        Course course = new Course();
        course.setName(request.getName());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            course.setTeacher(teacher);
        }
        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new RuntimeException("Semester not found"));
            course.setSemester(semester);
        }
        Course saved = courseRepository.save(course);
        return ApiResponse.ok(toCourseDTO(saved));
    }

    @PutMapping({"/courses/{id}", "/subjects/{id}"})
    public ApiResponse<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO request) {
        validateCourseDates(request);
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        course.setName(request.getName());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            course.setTeacher(teacher);
        }
        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new RuntimeException("Semester not found"));
            course.setSemester(semester);
        }
        Course saved = courseRepository.save(course);
        return ApiResponse.ok(toCourseDTO(saved));
    }

    @DeleteMapping({"/courses/{id}", "/subjects/{id}"})
    public ApiResponse<?> deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return ApiResponse.ok(Map.of("deleted", id));
    }

    // Subjects (real) — course assignment is via POST /course/{courseId}/subjects/{subjectId}
    @GetMapping("/subjects/all")
    public ApiResponse<List<SubjectDTO>> listSubjects() {
        List<SubjectDTO> list = subjectRepository.findAll().stream().map(subject -> {
            SubjectDTO dto = mapper.map(subject, SubjectDTO.class);
            dto.setTeacherId(subject.getTeacher() != null ? subject.getTeacher().getId() : null);
            dto.setTeacherName(subject.getTeacher() != null ? subject.getTeacher().getName() : null);
            if (subject.getCourseSubjects() != null && !subject.getCourseSubjects().isEmpty()) {
                var first = subject.getCourseSubjects().get(0).getCourse();
                dto.setCourseId(first != null ? first.getId() : null);
                dto.setCourseName(first != null ? first.getName() : null);
            }
            return dto;
        }).collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    @PostMapping("/subjects/create")
    public ApiResponse<SubjectDTO> createSubject(@RequestBody SubjectDTO request) {
        Subject subject = new Subject();
        subject.setName(request.getName());
        attachTeacher(subject, request.getTeacherId());
        Subject saved = subjectRepository.save(subject);
        SubjectDTO dto = mapper.map(saved, SubjectDTO.class);
        dto.setTeacherId(saved.getTeacher() != null ? saved.getTeacher().getId() : null);
        dto.setTeacherName(saved.getTeacher() != null ? saved.getTeacher().getName() : null);
        dto.setCourseId(request.getCourseId());
        dto.setCourseName(request.getCourseName());
        return ApiResponse.ok(dto);
    }

    @PutMapping("/subjects/update/{id}")
    public ApiResponse<SubjectDTO> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO request) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));
        subject.setName(request.getName());
        attachTeacher(subject, request.getTeacherId());
        Subject saved = subjectRepository.save(subject);
        SubjectDTO dto = mapper.map(saved, SubjectDTO.class);
        dto.setTeacherId(saved.getTeacher() != null ? saved.getTeacher().getId() : null);
        dto.setTeacherName(saved.getTeacher() != null ? saved.getTeacher().getName() : null);
        if (saved.getCourseSubjects() != null && !saved.getCourseSubjects().isEmpty()) {
            var first = saved.getCourseSubjects().get(0).getCourse();
            dto.setCourseId(first != null ? first.getId() : null);
            dto.setCourseName(first != null ? first.getName() : null);
        }
        return ApiResponse.ok(dto);
    }

    @DeleteMapping("/subjects/{id}")
    public ApiResponse<?> deleteSubject(@PathVariable Long id) {
        subjectRepository.deleteById(id);
        return ApiResponse.ok(Map.of("deleted", id));
    }

    /**
     * Attach teacher by either userId or teacherId; tolerate null.
     */
    private void attachTeacher(Subject subject, Long teacherId) {
        if (teacherId == null) {
            subject.setTeacher(null);
            return;
        }
        // Try user id first
        User teacherUser = userRepository.findById(teacherId).orElse(null);
        if (teacherUser != null) {
            subject.setTeacher(teacherUser);
            return;
        }
        // Fallback: teacherId from Teacher entity -> resolve its user
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher != null && teacher.getUser() != null) {
            subject.setTeacher(teacher.getUser());
        } else {
            throw new RuntimeException("Teacher not found for id: " + teacherId);
        }
    }

    @PostMapping("/courses/{courseId}/subjects")
    public ApiResponse<List<SubjectDTO>> assignSubjectsToCourse(@PathVariable Long courseId, @RequestBody Map<String, List<Long>> body) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Long> subjectIds = body.getOrDefault("subjectIds", List.of());
        List<SubjectDTO> updated = subjectIds.stream().map(id -> {
            courseService.addSubjectToCourse(courseId, id);
            Subject subject = subjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
            SubjectDTO dto = mapper.map(subject, SubjectDTO.class);
            dto.setTeacherId(subject.getTeacher() != null ? subject.getTeacher().getId() : null);
            dto.setTeacherName(subject.getTeacher() != null ? subject.getTeacher().getName() : null);
            dto.setCourseId(courseId);
            dto.setCourseName(course.getName());
            return dto;
        }).toList();
        return ApiResponse.ok(updated);
    }

    // Admin management
    @GetMapping("/manage")
    public ApiResponse<List<User>> getAdmins() {
        return ApiResponse.ok(userRepository.findAllByRole(Role.SUPER_ADMIN));
    }

    @DeleteMapping("/manage/{id}")
    public ApiResponse<?> deleteAdmin(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ApiResponse.ok(Map.of("deleted", id));
    }

    private CourseDTO toCourseDTO(Course course) {
        CourseDTO dto = mapper.map(course, CourseDTO.class);
        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
            dto.setTeacherName(course.getTeacher().getName());
        }
        if (course.getSemester() != null) {
            dto.setSemesterId(course.getSemester().getId());
            dto.setSemesterName(course.getSemester().getName());
        }
        if (course.getCourseStudents() != null) {
            dto.setEnrolledStudentIds(
                    course.getCourseStudents().stream()
                            .map(cs -> cs.getStudent() != null ? cs.getStudent().getId() : null)
                            .filter(id -> id != null)
                            .toList());
        }
        return dto;
    }

    private void validateCourseDates(CourseDTO dto) {
        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("endDate must be on or after startDate");
        }
    }
}
