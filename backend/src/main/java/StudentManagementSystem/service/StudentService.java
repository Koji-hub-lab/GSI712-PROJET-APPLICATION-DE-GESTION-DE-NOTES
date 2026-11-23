package StudentManagementSystem.service;


import StudentManagementSystem.entity.Student;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public Student addStudent(Student student) {
        log.info("Adding new student: {}", student.getName());
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        log.debug("Fetching all students");
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        log.debug("Fetching student by id: {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public void deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
        studentRepository.deleteById(id);
    }
}
