package StudentManagementSystem.service;

import StudentManagementSystem.dto.SubjectLiteDTO;
import StudentManagementSystem.dto.TeacherCourseAssignmentsDTO;
import StudentManagementSystem.dto.TeacherCourseStudentDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.CourseStudent;
import StudentManagementSystem.entity.CourseSubject;
import StudentManagementSystem.entity.Enrollment;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherAssignmentService {

    private final CourseService courseService;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;

    @Transactional(readOnly = true)
    public TeacherCourseAssignmentsDTO getCourseAssignments(Long courseId, Long teacherId) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        Course course = courseService.getCourseById(courseId);
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourse_Id(courseId);
        List<SubjectLiteDTO> availableSubjects = courseSubjects.stream()
                .map(CourseSubject::getSubject)
                .filter(subject -> subject != null && subject.getId() != null)
                .map(subject -> new SubjectLiteDTO(subject.getId(), subject.getName()))
                .toList();

        List<CourseStudent> courseStudents = courseStudentRepository.findByCourse_Id(courseId);
        List<Long> studentIds = courseStudents.stream()
                .map(CourseStudent::getStudent)
                .filter(student -> student != null && student.getId() != null)
                .map(student -> student.getId())
                .toList();

        Map<Long, List<SubjectLiteDTO>> assignedByStudent = new LinkedHashMap<>();
        if (!studentIds.isEmpty()) {
            List<Enrollment> enrollments = enrollmentRepository.findByCourse_IdAndStudent_IdIn(courseId, studentIds);
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null) {
                    continue;
                }
                Subject subject = enrollment.getSubject();
                if (subject == null || subject.getId() == null) {
                    continue;
                }
                List<SubjectLiteDTO> subjects = assignedByStudent.computeIfAbsent(
                        enrollment.getStudent().getId(), ignored -> new ArrayList<>());
                boolean exists = subjects.stream().anyMatch(s -> s.getId().equals(subject.getId()));
                if (!exists) {
                    subjects.add(new SubjectLiteDTO(subject.getId(), subject.getName()));
                }
            }
        }

        List<TeacherCourseStudentDTO> students = courseStudents.stream()
                .map(CourseStudent::getStudent)
                .filter(student -> student != null && student.getId() != null)
                .map(student -> new TeacherCourseStudentDTO(
                        student.getId(),
                        student.getName(),
                        student.getEmail(),
                        assignedByStudent.getOrDefault(student.getId(), List.of())))
                .toList();

        return new TeacherCourseAssignmentsDTO(
                course.getId(),
                course.getName(),
                availableSubjects,
                students
        );
    }

    @Transactional
    public TeacherCourseStudentDTO assignSubjectsToStudent(
            Long courseId,
            Long studentId,
            Long teacherId,
            List<Long> subjectIds
    ) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }

        if (!courseStudentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId)) {
            throw new IllegalArgumentException("Student is not enrolled in this course.");
        }
        if (subjectIds == null || subjectIds.isEmpty()) {
            throw new IllegalArgumentException("At least one subject is required.");
        }

        Set<Long> requestedIds = new LinkedHashSet<>(subjectIds);
        Set<Long> courseSubjectIds = courseSubjectRepository.findByCourse_Id(courseId).stream()
                .map(CourseSubject::getSubject)
                .filter(subject -> subject != null && subject.getId() != null)
                .map(Subject::getId)
                .collect(Collectors.toSet());

        List<Long> invalidSubjectIds = requestedIds.stream()
                .filter(id -> !courseSubjectIds.contains(id))
                .toList();
        if (!invalidSubjectIds.isEmpty()) {
            throw new IllegalArgumentException("Subjects do not belong to this course: " + invalidSubjectIds);
        }

        List<Enrollment> existing = enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, courseId);
        Set<Long> existingSubjectIds = existing.stream()
                .map(Enrollment::getSubject)
                .filter(subject -> subject != null && subject.getId() != null)
                .map(Subject::getId)
                .collect(Collectors.toSet());

        for (Long subjectId : requestedIds) {
            if (existingSubjectIds.contains(subjectId)) {
                continue;
            }
            enrollmentService.addEnrollment(new Enrollment(), studentId, courseId, subjectId);
        }

        List<Enrollment> updated = enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, courseId);
        List<SubjectLiteDTO> assignedSubjects = updated.stream()
                .map(Enrollment::getSubject)
                .filter(subject -> subject != null && subject.getId() != null)
                .map(subject -> new SubjectLiteDTO(subject.getId(), subject.getName()))
                .toList();

        CourseStudent courseStudent = courseStudentRepository.findByCourse_IdAndStudent_Id(courseId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student-course link not found"));

        return new TeacherCourseStudentDTO(
                courseStudent.getStudent().getId(),
                courseStudent.getStudent().getName(),
                courseStudent.getStudent().getEmail(),
                assignedSubjects
        );
    }
}

