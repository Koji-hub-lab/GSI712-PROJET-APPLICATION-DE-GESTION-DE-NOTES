package StudentManagementSystem.controller;

import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.AssignmentDTO;
import StudentManagementSystem.dto.AssignmentScoreRequestDTO;
import StudentManagementSystem.dto.StudentAssignmentScoreDTO;
import StudentManagementSystem.dto.StudentSubjectGradeDTO;
import StudentManagementSystem.dto.SubjectGradeRequestDTO;
import StudentManagementSystem.service.StudentGradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentGradingController {

    private final StudentGradingService gradingService;

    @PostMapping("/assignments/create")
    public ApiResponse<AssignmentDTO> createAssignment(@RequestBody AssignmentDTO dto) {
        return ApiResponse.ok(gradingService.createAssignment(dto));
    }

    @GetMapping("/courses/{courseId}/subjects/{subjectId}/assignments")
    public ApiResponse<List<AssignmentDTO>> getAssignmentsBySubject(
            @PathVariable Long courseId,
            @PathVariable Long subjectId,
            @RequestParam(required = false) Long teacherId
    ) {
        return ApiResponse.ok(gradingService.getAssignmentsByCourseAndSubject(courseId, subjectId, teacherId));
    }

    @PostMapping("/assignments/{assignmentId}/grade")
    public ApiResponse<StudentAssignmentScoreDTO> gradeAssignment(
            @PathVariable Long assignmentId,
            @RequestBody AssignmentScoreRequestDTO request
    ) {
        return ApiResponse.ok(gradingService.gradeAssignment(
                assignmentId,
                request.getStudentId(),
                request.getTeacherId(),
                request.getScore(),
                request.getFeedback()));
    }

    @GetMapping("/assignments/{assignmentId}/scores")
    public ApiResponse<List<StudentAssignmentScoreDTO>> getAssignmentScores(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) Long teacherId
    ) {
        return ApiResponse.ok(gradingService.getAssignmentScores(assignmentId, teacherId));
    }

    @PostMapping("/subjects/{subjectId}/students/{studentId}/grade")
    public ApiResponse<StudentSubjectGradeDTO> gradeSubject(
            @PathVariable Long subjectId,
            @PathVariable Long studentId,
            @RequestBody SubjectGradeRequestDTO request
    ) {
        return ApiResponse.ok(gradingService.gradeSubject(
                subjectId,
                studentId,
                request.getCourseId(),
                request.getTeacherId(),
                request.getTotalScore(),
                request.getGrade()));
    }

    @GetMapping("/courses/{courseId}/subjects/{subjectId}/grades")
    public ApiResponse<List<StudentSubjectGradeDTO>> getCourseSubjectGrades(
            @PathVariable Long courseId,
            @PathVariable Long subjectId,
            @RequestParam(required = false) Long teacherId
    ) {
        return ApiResponse.ok(gradingService.getCourseSubjectGrades(courseId, subjectId, teacherId));
    }

    @GetMapping("/student/{studentId}/grades")
    public ApiResponse<List<StudentSubjectGradeDTO>> getStudentGrades(@PathVariable Long studentId) {
        return ApiResponse.ok(gradingService.getStudentSubjectGrades(studentId));
    }

    @GetMapping("/student/{studentId}/assignments")
    public ApiResponse<List<StudentAssignmentScoreDTO>> getStudentAssignmentScores(@PathVariable Long studentId) {
        return ApiResponse.ok(gradingService.getStudentAssignmentScores(studentId));
    }
}
