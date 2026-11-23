package StudentManagementSystem.service;

import StudentManagementSystem.dto.CourseMaterialDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.CourseMaterial;
import StudentManagementSystem.entity.NotificationType;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.entity.Teacher;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.CourseMaterialRepository;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.SubjectRepository;
import StudentManagementSystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseMaterialService {

    private final CourseMaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final CourseService courseService;
    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final NotificationService notificationService;

    @Transactional
    public CourseMaterialDTO upload(CourseMaterialDTO dto) {
        if (dto.getCourseId() == null || dto.getSubjectId() == null || dto.getUploadedById() == null) {
            throw new IllegalArgumentException("courseId, subjectId and uploadedById are required.");
        }
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(dto.getUploadedById())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        courseService.ensureTeacherOwnsCourse(course.getId(), teacher.getId());
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(course.getId(), subject.getId())) {
            throw new IllegalArgumentException("Subject does not belong to this course.");
        }

        CourseMaterial material = new CourseMaterial();
        material.setCourse(course);
        material.setSubject(subject);
        material.setTitle(dto.getTitle());
        material.setFileUrl(dto.getFileUrl());
        material.setUploadedBy(teacher);
        material.setUploadedAt(LocalDateTime.now());
        CourseMaterial saved = materialRepository.save(material);

        notificationService.createForCourseStudents(
                course.getId(),
                "New Study Material",
                "New material '" + saved.getTitle() + "' uploaded for " + course.getName(),
                NotificationType.MATERIAL_UPLOAD
        );
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseMaterialDTO> getByCourse(Long courseId, Long teacherId) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        return materialRepository.findByCourse_Id(courseId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseMaterialDTO> getForStudent(Long studentId, Long courseId) {
        if (courseId != null && !courseStudentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId)) {
            throw new IllegalArgumentException("Student is not enrolled in this course.");
        }
        if (courseId != null) {
            return materialRepository.findByCourse_Id(courseId).stream().map(this::toDto).toList();
        }
        List<Long> courseIds = courseStudentRepository.findByStudent_Id(studentId).stream()
                .map(link -> link.getCourse().getId())
                .toList();
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return materialRepository.findByCourse_IdIn(courseIds).stream().map(this::toDto).toList();
    }

    private CourseMaterialDTO toDto(CourseMaterial material) {
        return new CourseMaterialDTO(
                material.getId(),
                material.getCourse() != null ? material.getCourse().getId() : null,
                material.getCourse() != null ? material.getCourse().getName() : null,
                material.getSubject() != null ? material.getSubject().getId() : null,
                material.getSubject() != null ? material.getSubject().getName() : null,
                material.getTitle(),
                material.getFileUrl(),
                material.getUploadedBy() != null ? material.getUploadedBy().getId() : null,
                material.getUploadedBy() != null ? material.getUploadedBy().getName() : null,
                material.getUploadedAt()
        );
    }
}

