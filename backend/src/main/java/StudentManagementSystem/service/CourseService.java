package StudentManagementSystem.service;

import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.CourseStudent;
import StudentManagementSystem.entity.CourseSubject;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.StudentRepository;
import StudentManagementSystem.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    /** Assign a subject to a course (DB: CourseSubject). Required before students can enroll in that subject in this course. */
    public CourseSubject addSubjectToCourse(Long courseId, Long subjectId) {
        Course course = getCourseById(courseId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        if (courseSubjectRepository.existsByCourse_IdAndSubject_Id(courseId, subjectId)) {
            throw new IllegalStateException("Subject is already assigned to this course.");
        }
        try {
            return courseSubjectRepository.save(CourseSubject.builder().course(course).subject(subject).build());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Cannot assign subject to course: " + e.getMostSpecificCause().getMessage(), e);
        }
    }

    /** Enroll a student in a course (DB: CourseStudent). Required before student can enroll in subjects of this course. */
    public CourseStudent addStudentToCourse(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        if (courseStudentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId)) {
            throw new IllegalStateException("Student is already enrolled in this course.");
        }
        try {
            return courseStudentRepository.save(CourseStudent.builder().course(course).student(student).build());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Cannot enroll student in course: " + e.getMostSpecificCause().getMessage(), e);
        }
    }

    /** Throws if the course is not assigned to the given teacher. Use for teacher-scoped operations. */
    public void ensureTeacherOwnsCourse(Long courseId, Long teacherId) {
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        Course course = getCourseById(courseId);
        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacherId)) {
            throw new AccessDeniedException("Teacher can only manage courses assigned to them. Course is not assigned to this teacher.");
        }
    }

    /** For use when teacher context is available: return only courses assigned to this teacher. */
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        if (teacherId == null) return getAllCourses();
        return courseRepository.findAll().stream()
                .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacherId))
                .toList();
    }

    /** Courses the student is enrolled in (CourseStudent). */
    public List<Course> getCoursesByStudentId(Long studentId) {
        return courseStudentRepository.findByStudent_Id(studentId).stream()
                .map(CourseStudent::getCourse)
                .toList();
    }

    /** Subjects assigned to this course (CourseSubject). */
    public List<Subject> getSubjectsInCourse(Long courseId) {
        return courseSubjectRepository.findByCourse_Id(courseId).stream()
                .map(CourseSubject::getSubject)
                .toList();
    }
}
