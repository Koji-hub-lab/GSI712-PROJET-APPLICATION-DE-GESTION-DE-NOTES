package StudentManagementSystem.controller;


import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.StudentDTO;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final ModelMapper mapper;

    @GetMapping("/all")
    public ApiResponse<List<StudentDTO>> getAllStudents() {
        return ApiResponse.ok(studentService.getAllStudents()
                .stream().map(s -> mapper.map(s, StudentDTO.class))
                .collect(Collectors.toList()));
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<StudentDTO> getStudentById(@PathVariable Long id) {
        return ApiResponse.ok(mapper.map(studentService.getStudentById(id), StudentDTO.class));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ApiResponse.ok(java.util.Map.of("deleted", id));
    }

    @PostMapping("/add")
    public ApiResponse<StudentDTO> addStudent(@Valid @RequestBody StudentDTO studentDTO) {
        Student student = mapper.map(studentDTO, Student.class);
        return ApiResponse.ok(mapper.map(studentService.addStudent(student), StudentDTO.class));
    }
}
