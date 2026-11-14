package StudentManagementSystem.controller;

import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.CourseDTO;
import StudentManagementSystem.dto.SubjectDTO;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final ModelMapper mapper;

    @GetMapping("/all")
    public ApiResponse<List<SubjectDTO>> getAllSubjects() {
        return ApiResponse.ok(subjectService.getAllSubjects()
                .stream().map(s -> {
                    SubjectDTO dto = mapper.map(s, SubjectDTO.class);
                    dto.setTeacherId(s.getTeacher() != null ? s.getTeacher().getId() : null);
                    dto.setTeacherName(s.getTeacher() != null ? s.getTeacher().getName() : null);
                    // Course comes from CourseSubject join (subject can be in multiple courses)
                    if (s.getCourseSubjects() != null && !s.getCourseSubjects().isEmpty()) {
                        var first = s.getCourseSubjects().get(0).getCourse();
                        dto.setCourseId(first != null ? first.getId() : null);
                        dto.setCourseName(first != null ? first.getName() : null);
                    }
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    @PostMapping("/add")
    public ApiResponse<SubjectDTO> addSubject(@RequestBody SubjectDTO subjectDTO) {
        Subject subject = mapper.map(subjectDTO, Subject.class);
        return ApiResponse.ok(mapper.map(subjectService.addSubject(subject), SubjectDTO.class));
    }
}
