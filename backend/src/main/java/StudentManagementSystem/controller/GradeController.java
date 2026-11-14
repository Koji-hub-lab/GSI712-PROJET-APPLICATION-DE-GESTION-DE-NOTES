package StudentManagementSystem.controller;


import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.dto.GradeDTO;
import StudentManagementSystem.entity.Grade;
import StudentManagementSystem.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final ModelMapper mapper;

    @GetMapping("/all")
    public ApiResponse<List<GradeDTO>> getAllGrades() {
        return ApiResponse.ok(gradeService.getAllGrades()
                .stream().map(g -> mapper.map(g, GradeDTO.class))
                .collect(Collectors.toList()));
    }

    @PostMapping("/add")
    public ApiResponse<GradeDTO> addGrade(@RequestBody GradeDTO dto) {
        Grade grade = mapper.map(dto, Grade.class);
        return ApiResponse.ok(mapper.map(gradeService.addGrade(grade), GradeDTO.class));
    }
}
