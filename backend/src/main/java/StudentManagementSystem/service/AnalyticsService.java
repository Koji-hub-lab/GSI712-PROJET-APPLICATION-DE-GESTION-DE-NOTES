package StudentManagementSystem.service;

import StudentManagementSystem.dto.AdminAnalyticsDTO;
import StudentManagementSystem.dto.TeacherAnalyticsDTO;
import StudentManagementSystem.entity.AssignmentSubmission;
import StudentManagementSystem.entity.Attendance;
import StudentManagementSystem.entity.AttendanceStatus;
import StudentManagementSystem.repository.AssignmentRepository;
import StudentManagementSystem.repository.AssignmentSubmissionRepository;
import StudentManagementSystem.repository.AttendanceRepository;
import StudentManagementSystem.repository.CourseCompletionRepository;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.EnrollmentRepository;
import StudentManagementSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseCompletionRepository completionRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final AttendanceRepository attendanceRepository;
    private final CourseService courseService;

    @Transactional(readOnly = true)
    public AdminAnalyticsDTO getAdminAnalytics() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long totalCourseStudentLinks = courseStudentRepository.count();
        long completed = completionRepository.count();
        double completionRate = totalCourseStudentLinks == 0
                ? 0.0
                : (completed * 100.0) / totalCourseStudentLinks;
        return new AdminAnalyticsDTO(totalStudents, totalCourses, totalEnrollments, completionRate);
    }

    @Transactional(readOnly = true)
    public TeacherAnalyticsDTO getTeacherAnalytics(Long teacherId) {
        List<Long> managedCourseIds = courseService.getCoursesByTeacherId(teacherId).stream()
                .map(course -> course.getId())
                .toList();

        if (managedCourseIds.isEmpty()) {
            return new TeacherAnalyticsDTO(0, 0, 0, 0, 0);
        }

        long managedStudents = courseStudentRepository.findByCourse_IdIn(managedCourseIds).stream()
                .map(link -> link.getStudent().getId())
                .distinct()
                .count();

        List<AssignmentSubmission> submissions = submissionRepository.findAll().stream()
                .filter(sub -> sub.getAssignment() != null
                        && sub.getAssignment().getCourse() != null
                        && managedCourseIds.contains(sub.getAssignment().getCourse().getId()))
                .toList();
        double averageScore = submissions.stream()
                .filter(sub -> sub.getScore() != null)
                .mapToDouble(AssignmentSubmission::getScore)
                .average()
                .orElse(0.0);

        List<Attendance> attendance = attendanceRepository.findAll().stream()
                .filter(row -> row.getCourse() != null && managedCourseIds.contains(row.getCourse().getId()))
                .toList();
        long present = attendance.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        double attendancePercentage = attendance.isEmpty() ? 0.0 : (present * 100.0) / attendance.size();

        long assignmentsCount = assignmentRepository.findByCourse_IdIn(managedCourseIds).size();
        double assignmentCompletionRate = assignmentsCount == 0 || managedStudents == 0
                ? 0.0
                : (submissions.size() * 100.0) / (assignmentsCount * managedStudents);

        return new TeacherAnalyticsDTO(
                managedCourseIds.size(),
                managedStudents,
                averageScore,
                attendancePercentage,
                assignmentCompletionRate
        );
    }
}

