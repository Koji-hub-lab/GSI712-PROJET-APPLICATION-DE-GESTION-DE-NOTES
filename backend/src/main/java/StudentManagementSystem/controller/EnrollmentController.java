package StudentManagementSystem.controller;


import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.EnrollmentDTO;
import StudentManagementSystem.entity.Enrollment;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/all")
    public ApiResponse<List<EnrollmentDTO>> getAllEnrollments() {
        return ApiResponse.ok(enrollmentService.getAllEnrollments()
                .stream().map(this::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addEnrollment(@RequestBody EnrollmentDTO dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Enrollment payload is required."));
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(dto.getStatus());

        try {
            Enrollment saved = enrollmentService.addEnrollment(
                    enrollment,
                    dto.getStudentId(),
                    dto.getCourseId(),
                    dto.getSubjectId());
            return ResponseEntity.ok(ApiResponse.ok(toDto(saved)));
        } catch (IllegalStateException dup) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(dup.getMessage()));
        } catch (IllegalArgumentException | org.springframework.security.access.AccessDeniedException badRequest) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(badRequest.getMessage()));
        } catch (ResourceNotFoundException notFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound.getMessage()));
        } catch (DataIntegrityViolationException dive) {
            Throwable cause = dive.getMostSpecificCause();
            String message = cause != null ? cause.getMessage() : "Data integrity violation";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(message));
        }
    }

    private EnrollmentDTO toDto(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStatus(enrollment.getStatus());

        if (enrollment.getStudent() != null) {
            dto.setStudentId(enrollment.getStudent().getId());
            dto.setStudentName(enrollment.getStudent().getName());
        }
        if (enrollment.getCourse() != null) {
            dto.setCourseId(enrollment.getCourse().getId());
            dto.setCourseName(enrollment.getCourse().getName());
        }
        if (enrollment.getSubject() != null) {
            dto.setSubjectId(enrollment.getSubject().getId());
            dto.setSubjectName(enrollment.getSubject().getName());
        }
        return dto;
    }
}
