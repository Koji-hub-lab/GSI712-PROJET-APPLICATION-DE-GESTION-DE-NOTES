package StudentManagementSystem.service;

import StudentManagementSystem.dto.AttendanceDTO;
import StudentManagementSystem.entity.Attendance;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.entity.Teacher;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.AttendanceRepository;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.StudentRepository;
import StudentManagementSystem.repository.SubjectRepository;
import StudentManagementSystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseService courseService;

    @Transactional
    public AttendanceDTO recordAttendance(AttendanceDTO dto) {
        if (dto.getStudentId() == null || dto.getSubjectId() == null || dto.getCourseId() == null || dto.getDate() == null) {
            throw new IllegalArgumentException("studentId, subjectId, courseId and date are required.");
        }

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!courseStudentRepository.existsByCourse_IdAndStudent_Id(course.getId(), student.getId())) {
            throw new IllegalArgumentException("Student is not enrolled in this course.");
        }
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(course.getId(), subject.getId())) {
            throw new IllegalArgumentException("Subject does not belong to this course.");
        }

        Teacher teacher = null;
        if (dto.getRecordedById() != null) {
            teacher = teacherRepository.findById(dto.getRecordedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            courseService.ensureTeacherOwnsCourse(course.getId(), teacher.getId());
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setCourse(course);
        attendance.setDate(dto.getDate());
        attendance.setStatus(dto.getStatus());
        attendance.setRecordedBy(teacher);
        return toDto(attendanceRepository.save(attendance));
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getByCourse(Long courseId, Long teacherId) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        return attendanceRepository.findByCourse_Id(courseId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getByStudent(Long studentId, Long courseId) {
        List<Attendance> rows = courseId != null
                ? attendanceRepository.findByStudent_IdAndCourse_Id(studentId, courseId)
                : attendanceRepository.findByStudent_Id(studentId);
        return rows.stream().map(this::toDto).toList();
    }

    private AttendanceDTO toDto(Attendance attendance) {
        return new AttendanceDTO(
                attendance.getId(),
                attendance.getStudent() != null ? attendance.getStudent().getId() : null,
                attendance.getStudent() != null ? attendance.getStudent().getName() : null,
                attendance.getSubject() != null ? attendance.getSubject().getId() : null,
                attendance.getSubject() != null ? attendance.getSubject().getName() : null,
                attendance.getCourse() != null ? attendance.getCourse().getId() : null,
                attendance.getCourse() != null ? attendance.getCourse().getName() : null,
                attendance.getDate(),
                attendance.getStatus(),
                attendance.getRecordedBy() != null ? attendance.getRecordedBy().getId() : null,
                attendance.getRecordedBy() != null ? attendance.getRecordedBy().getName() : null
        );
    }
}

