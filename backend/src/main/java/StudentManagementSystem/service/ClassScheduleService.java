package StudentManagementSystem.service;

import StudentManagementSystem.dto.ClassScheduleDTO;
import StudentManagementSystem.entity.ClassSchedule;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.entity.Teacher;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.ClassScheduleRepository;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.SubjectRepository;
import StudentManagementSystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {

    private final ClassScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseService courseService;

    @Transactional
    public ClassScheduleDTO create(ClassScheduleDTO dto) {
        if (dto.getCourseId() == null || dto.getSubjectId() == null || dto.getTeacherId() == null) {
            throw new IllegalArgumentException("courseId, subjectId and teacherId are required.");
        }
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        courseService.ensureTeacherOwnsCourse(course.getId(), teacher.getId());
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(course.getId(), subject.getId())) {
            throw new IllegalArgumentException("Subject does not belong to this course.");
        }

        ClassSchedule schedule = new ClassSchedule();
        schedule.setCourse(course);
        schedule.setSubject(subject);
        schedule.setTeacher(teacher);
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setClassroom(dto.getClassroom());
        return toDto(scheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public List<ClassScheduleDTO> getByCourse(Long courseId, Long teacherId) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        return scheduleRepository.findByCourse_Id(courseId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ClassScheduleDTO> getByStudent(Long studentId, Long courseId) {
        if (courseId != null) {
            if (!courseStudentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId)) {
                throw new IllegalArgumentException("Student is not enrolled in this course.");
            }
            return scheduleRepository.findByCourse_Id(courseId).stream().map(this::toDto).toList();
        }
        List<Long> courseIds = courseStudentRepository.findByStudent_Id(studentId).stream()
                .map(link -> link.getCourse().getId())
                .toList();
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return scheduleRepository.findByCourse_IdIn(courseIds).stream().map(this::toDto).toList();
    }

    private ClassScheduleDTO toDto(ClassSchedule schedule) {
        return new ClassScheduleDTO(
                schedule.getId(),
                schedule.getCourse() != null ? schedule.getCourse().getId() : null,
                schedule.getCourse() != null ? schedule.getCourse().getName() : null,
                schedule.getSubject() != null ? schedule.getSubject().getId() : null,
                schedule.getSubject() != null ? schedule.getSubject().getName() : null,
                schedule.getTeacher() != null ? schedule.getTeacher().getId() : null,
                schedule.getTeacher() != null ? schedule.getTeacher().getName() : null,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getClassroom()
        );
    }
}

