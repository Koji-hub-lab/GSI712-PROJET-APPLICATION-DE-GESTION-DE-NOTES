package StudentManagementSystem.controller;

import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.TeacherDTO;
import StudentManagementSystem.entity.Teacher;
import StudentManagementSystem.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final ModelMapper mapper;

    @GetMapping("/all")
    public ApiResponse<List<TeacherDTO>> getAllTeachers() {
        return ApiResponse.ok(teacherService.getAllTeachers()
                .stream().map(t -> mapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList()));
    }

    @PostMapping("/add")
    public ApiResponse<TeacherDTO> addTeacher(@Valid @RequestBody TeacherDTO teacherDTO) {
        Teacher teacher = mapper.map(teacherDTO, Teacher.class);
        return ApiResponse.ok(mapper.map(teacherService.addTeacher(teacher), TeacherDTO.class));
    }


}
