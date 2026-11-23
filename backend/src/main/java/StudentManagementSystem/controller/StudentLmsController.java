package StudentManagementSystem.controller;

import StudentManagementSystem.dto.*;
import StudentManagementSystem.service.AssignmentService;
import StudentManagementSystem.service.AttendanceService;
import StudentManagementSystem.service.ClassScheduleService;
import StudentManagementSystem.service.CourseCompletionService;
import StudentManagementSystem.service.CourseMaterialService;
import StudentManagementSystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student/lms")
@RequiredArgsConstructor
public class StudentLmsController {

    private final AttendanceService attendanceService;
    private final AssignmentService assignmentService;
    private final CourseMaterialService materialService;
    private final ClassScheduleService scheduleService;
    private final NotificationService notificationService;
    private final CourseCompletionService completionService;

    @GetMapping("/attendance")
    public ApiResponse<List<AttendanceDTO>> myAttendance(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(attendanceService.getByStudent(studentId, courseId), page, size));
    }

    @GetMapping("/assignments")
    public ApiResponse<List<AssignmentDTO>> myAssignments(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(assignmentService.getByStudent(studentId), page, size));
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public ApiResponse<AssignmentSubmissionDTO> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestBody Map<String, Object> body
    ) {
        Long studentId = body.get("studentId") == null ? null : Long.valueOf(String.valueOf(body.get("studentId")));
        String fileUrl = body.get("fileUrl") == null ? null : String.valueOf(body.get("fileUrl"));
        return ApiResponse.ok(assignmentService.submit(assignmentId, studentId, fileUrl));
    }

    @GetMapping("/materials")
    public ApiResponse<List<CourseMaterialDTO>> myMaterials(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(materialService.getForStudent(studentId, courseId), page, size));
    }

    @GetMapping("/schedule")
    public ApiResponse<List<ClassScheduleDTO>> mySchedule(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(scheduleService.getByStudent(studentId, courseId), page, size));
    }

    @GetMapping("/notifications")
    public ApiResponse<List<NotificationDTO>> notifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(notificationService.getByUser(userId), page, size));
    }

    @PutMapping("/notifications/{notificationId}/read")
    public ApiResponse<NotificationDTO> markRead(@PathVariable Long notificationId, @RequestParam Long userId) {
        return ApiResponse.ok(notificationService.markRead(notificationId, userId));
    }

    @GetMapping("/completions")
    public ApiResponse<List<CourseCompletionDTO>> completions(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(completionService.getByStudent(studentId), page, size));
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
