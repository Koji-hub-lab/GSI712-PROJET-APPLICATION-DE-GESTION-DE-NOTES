package StudentManagementSystem.controller;

import StudentManagementSystem.dto.AdminAnalyticsDTO;
import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.SemesterDTO;
import StudentManagementSystem.entity.SemesterStatus;
import StudentManagementSystem.service.AnalyticsService;
import StudentManagementSystem.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/lms")
@RequiredArgsConstructor
public class AdminLmsController {

    private final SemesterService semesterService;
    private final AnalyticsService analyticsService;

    @PostMapping("/semesters")
    public ApiResponse<SemesterDTO> createSemester(@RequestBody SemesterDTO dto) {
        return ApiResponse.ok(semesterService.createSemester(dto));
    }

    @GetMapping("/semesters")
    public ApiResponse<List<SemesterDTO>> getSemesters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(paginate(semesterService.getAllSemesters(), page, size));
    }

    @PutMapping("/semesters/{semesterId}/status")
    public ApiResponse<SemesterDTO> updateSemesterStatus(
            @PathVariable Long semesterId,
            @RequestParam SemesterStatus status
    ) {
        return ApiResponse.ok(semesterService.updateSemesterStatus(semesterId, status));
    }

    @PutMapping("/courses/{courseId}/semester/{semesterId}")
    public ApiResponse<?> assignCourseToSemester(@PathVariable Long courseId, @PathVariable Long semesterId) {
        semesterService.assignCourseToSemester(courseId, semesterId);
        return ApiResponse.ok(java.util.Map.of("courseId", courseId, "semesterId", semesterId));
    }

    @GetMapping("/analytics")
    public ApiResponse<AdminAnalyticsDTO> adminAnalytics() {
        return ApiResponse.ok(analyticsService.getAdminAnalytics());
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
