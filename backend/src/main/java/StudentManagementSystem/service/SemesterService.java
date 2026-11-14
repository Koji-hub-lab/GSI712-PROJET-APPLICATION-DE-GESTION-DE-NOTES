package StudentManagementSystem.service;

import StudentManagementSystem.dto.SemesterDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.Semester;
import StudentManagementSystem.entity.SemesterStatus;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public SemesterDTO createSemester(SemesterDTO dto) {
        Semester semester = new Semester();
        semester.setName(dto.getName());
        semester.setStartDate(dto.getStartDate());
        semester.setEndDate(dto.getEndDate());
        semester.setStatus(dto.getStatus() != null ? dto.getStatus() : SemesterStatus.UPCOMING);
        return toDto(semesterRepository.save(semester));
    }

    @Transactional(readOnly = true)
    public List<SemesterDTO> getAllSemesters() {
        return semesterRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SemesterDTO updateSemesterStatus(Long semesterId, SemesterStatus status) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        semester.setStatus(status);
        return toDto(semesterRepository.save(semester));
    }

    @Transactional
    public void assignCourseToSemester(Long courseId, Long semesterId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        course.setSemester(semester);
        courseRepository.save(course);
    }

    private SemesterDTO toDto(Semester semester) {
        return new SemesterDTO(
                semester.getId(),
                semester.getName(),
                semester.getStartDate(),
                semester.getEndDate(),
                semester.getStatus()
        );
    }
}

