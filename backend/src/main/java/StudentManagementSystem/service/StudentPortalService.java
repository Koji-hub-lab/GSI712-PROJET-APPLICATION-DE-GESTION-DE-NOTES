package StudentManagementSystem.service;

import StudentManagementSystem.dto.StudentCourseDTO;
import StudentManagementSystem.dto.SubjectLiteDTO;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.CourseStudent;
import StudentManagementSystem.entity.CourseSubject;
import StudentManagementSystem.entity.Enrollment;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentPortalService {

    private final CourseStudentRepository courseStudentRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<StudentCourseDTO> getCourses(Long studentId) {
        List<CourseStudent> courseLinks = studentId != null
                ? courseStudentRepository.findByStudent_Id(studentId)
                : courseStudentRepository.findAll();

        if (courseLinks.isEmpty()) {
            return List.of();
        }

        Map<Long, StudentCourseDTO> byCourse = new LinkedHashMap<>();
        for (CourseStudent link : courseLinks) {
            Course course = link.getCourse();
            if (course == null || course.getId() == null) {
                continue;
            }
            byCourse.computeIfAbsent(course.getId(), id -> {
                StudentCourseDTO dto = new StudentCourseDTO();
                dto.setId(course.getId());
                dto.setName(course.getName());
                dto.setTeacherName(course.getTeacher() != null ? course.getTeacher().getName() : null);
                dto.setStartDate(course.getStartDate());
                dto.setEndDate(course.getEndDate());
                return dto;
            });
        }

        if (byCourse.isEmpty()) {
            return List.of();
        }

        List<Long> courseIds = new ArrayList<>(byCourse.keySet());
        Map<Long, List<SubjectLiteDTO>> subjectsByCourse = mapCourseSubjects(courseIds);
        byCourse.forEach((courseId, dto) -> dto.setSubjects(subjectsByCourse.getOrDefault(courseId, List.of())));

        if (studentId != null) {
            Map<Long, List<SubjectLiteDTO>> enrolledByCourse = mapStudentEnrolledSubjects(studentId, courseIds);
            byCourse.forEach((courseId, dto) -> dto.setEnrolledSubjects(enrolledByCourse.getOrDefault(courseId, List.of())));
        }

        return new ArrayList<>(byCourse.values());
    }

    private Map<Long, List<SubjectLiteDTO>> mapCourseSubjects(List<Long> courseIds) {
        Map<Long, List<SubjectLiteDTO>> out = new LinkedHashMap<>();
        List<CourseSubject> links = courseSubjectRepository.findByCourse_IdIn(courseIds);
        for (CourseSubject link : links) {
            if (link.getCourse() == null || link.getCourse().getId() == null) {
                continue;
            }
            Subject subject = link.getSubject();
            if (subject == null || subject.getId() == null) {
                continue;
            }
            out.computeIfAbsent(link.getCourse().getId(), ignored -> new ArrayList<>())
                    .add(new SubjectLiteDTO(subject.getId(), subject.getName()));
        }
        return out;
    }

    private Map<Long, List<SubjectLiteDTO>> mapStudentEnrolledSubjects(Long studentId, List<Long> courseIds) {
        Map<Long, List<SubjectLiteDTO>> out = new LinkedHashMap<>();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_IdAndCourse_IdIn(studentId, courseIds);
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null) {
                continue;
            }
            Subject subject = enrollment.getSubject();
            if (subject == null || subject.getId() == null) {
                continue;
            }
            List<SubjectLiteDTO> subjects = out.computeIfAbsent(enrollment.getCourse().getId(), ignored -> new ArrayList<>());
            boolean exists = subjects.stream().anyMatch(s -> s.getId().equals(subject.getId()));
            if (!exists) {
                subjects.add(new SubjectLiteDTO(subject.getId(), subject.getName()));
            }
        }
        return out;
    }
}

