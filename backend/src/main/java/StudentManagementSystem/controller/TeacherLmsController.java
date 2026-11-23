package StudentManagementSystem.controller;

import StudentManagementSystem.dto.*;
import StudentManagementSystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher/lms")
@RequiredArgsConstructor
public class TeacherLmsController {

    private final AttendanceService attendanceService;
    private final AssignmentService assignmentService;
    private final CourseMaterialService materialService;
    private final ClassScheduleService scheduleService;
    private final CourseCompletionService completionService;
    private final AnalyticsService analyticsService;

    @PostMapping("/attendance")
    public ApiResponse<AttendanceDTO> markAttendance(@RequestBody AttendanceDTO dto) {
        return ApiResponse.ok(attendanceService.recordAttendance(dto));
    }

    @GetMapping("/attendance")
    public ApiResponse<List<AttendanceDTO>> attendanceByCourse(
            @RequestParam Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(attendanceService.getByCourse(courseId, teacherId), page, size));
    }

    @PostMapping("/assignments")
    public ApiResponse<AssignmentDTO> createAssignment(@RequestBody AssignmentDTO dto) {
        return ApiResponse.ok(assignmentService.createAssignment(dto));
    }

    @GetMapping("/assignments")
    public ApiResponse<List<AssignmentDTO>> assignmentsByCourse(
            @RequestParam Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(assignmentService.getByCourse(courseId, teacherId), page, size));
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ApiResponse<List<AssignmentSubmissionDTO>> submissions(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(assignmentService.getSubmissions(assignmentId, teacherId), page, size));
    }

    @PutMapping("/assignments/{assignmentId}/submissions/{studentId}/grade")
    public ApiResponse<AssignmentSubmissionDTO> gradeSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long studentId,
            @RequestBody Map<String, Object> body
    ) {
        Long teacherId = body.get("teacherId") == null ? null : Long.valueOf(String.valueOf(body.get("teacherId")));
        Double score = body.get("score") == null ? null : Double.valueOf(String.valueOf(body.get("score")));
        String feedback = body.get("feedback") == null ? null : String.valueOf(body.get("feedback"));
        return ApiResponse.ok(assignmentService.gradeSubmission(assignmentId, studentId, teacherId, score, feedback));
    }

    @PostMapping("/materials")
    public ApiResponse<CourseMaterialDTO> uploadMaterial(@RequestBody CourseMaterialDTO dto) {
        return ApiResponse.ok(materialService.upload(dto));
    }

    @GetMapping("/materials")
    public ApiResponse<List<CourseMaterialDTO>> materials(
            @RequestParam Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(materialService.getByCourse(courseId, teacherId), page, size));
    }

    @PostMapping("/schedule")
    public ApiResponse<ClassScheduleDTO> createSchedule(@RequestBody ClassScheduleDTO dto) {
        return ApiResponse.ok(scheduleService.create(dto));
    }

    @GetMapping("/schedule")
    public ApiResponse<List<ClassScheduleDTO>> scheduleByCourse(
            @RequestParam Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(scheduleService.getByCourse(courseId, teacherId), page, size));
    }

    @PostMapping("/completions")
    public ApiResponse<CourseCompletionDTO> recordCompletion(@RequestBody CourseCompletionDTO dto) {
        return ApiResponse.ok(completionService.recordCompletion(dto));
    }

    @GetMapping("/analytics")
    public ApiResponse<TeacherAnalyticsDTO> analytics(@RequestParam Long teacherId) {
        return ApiResponse.ok(analyticsService.getTeacherAnalytics(teacherId));
    }

    private <T> List<T> paginate(List<T> list, int page, int size) {
        if (size <= 0) return list;
        int from = Math.max(0, page) * size;
        if (from >= list.size()) {
            return List.of();
        }
        int to = Math.min(list.size(), from + size);
        return list.subList(from, to);
    }
}
