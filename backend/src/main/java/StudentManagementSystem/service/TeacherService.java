package StudentManagementSystem.service;


import StudentManagementSystem.entity.Teacher;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public Teacher addTeacher(Teacher teacher) {
        log.info("Adding new teacher: {}", teacher.getName());
        return teacherRepository.save(teacher);
    }

    public List<Teacher> getAllTeachers() {
        log.debug("Fetching all teachers");
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        log.debug("Fetching teacher by id: {}", id);
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    public void deleteTeacher(Long id) {
        log.info("Deleting teacher with id: {}", id);
        teacherRepository.deleteById(id);
    }
}
