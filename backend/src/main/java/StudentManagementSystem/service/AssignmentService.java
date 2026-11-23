package StudentManagementSystem.service;

import StudentManagementSystem.dto.AssignmentDTO;
import StudentManagementSystem.dto.AssignmentSubmissionDTO;
import StudentManagementSystem.entity.Assignment;
import StudentManagementSystem.entity.AssignmentSubmission;
import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.NotificationType;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.entity.Teacher;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.AssignmentRepository;
import StudentManagementSystem.repository.AssignmentSubmissionRepository;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.StudentRepository;
import StudentManagementSystem.repository.SubjectRepository;
import StudentManagementSystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseService courseService;
    private final NotificationService notificationService;

    @Transactional
    public AssignmentDTO createAssignment(AssignmentDTO dto) {
        if (dto.getCourseId() == null || dto.getSubjectId() == null || dto.getCreatedById() == null) {
            throw new IllegalArgumentException("courseId, subjectId and createdById are required.");
        }

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        courseService.ensureTeacherOwnsCourse(course.getId(), teacher.getId());
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(course.getId(), subject.getId())) {
            throw new IllegalArgumentException("Subject does not belong to this course.");
        }

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setSubject(subject);
        assignment.setCreatedBy(teacher);
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(dto.getDueDate());
        assignment.setMaxScore(dto.getMaxScore());
        assignment.setCreatedAt(LocalDateTime.now());
        Assignment saved = assignmentRepository.save(assignment);

        notificationService.createForCourseStudents(
                course.getId(),
                "New Assignment",
                "A new assignment '" + saved.getTitle() + "' was posted in " + course.getName(),
                NotificationType.ASSIGNMENT_NEW
        );
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getByCourse(Long courseId, Long teacherId) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        return assignmentRepository.findByCourse_Id(courseId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getByStudent(Long studentId) {
        List<Long> courseIds = courseStudentRepository.findByStudent_Id(studentId).stream()
                .map(link -> link.getCourse().getId())
                .toList();
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return assignmentRepository.findByCourse_IdIn(courseIds).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AssignmentSubmissionDTO submit(Long assignmentId, Long studentId, String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("fileUrl is required.");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!courseStudentRepository.existsByCourse_IdAndStudent_Id(assignment.getCourse().getId(), student.getId())) {
            throw new IllegalArgumentException("Student is not enrolled in assignment course.");
        }

        AssignmentSubmission submission = submissionRepository
                .findByAssignment_IdAndStudent_Id(assignmentId, studentId)
                .orElse(new AssignmentSubmission());
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setFileUrl(fileUrl);
        submission.setSubmittedAt(LocalDateTime.now());
        return toDto(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public List<AssignmentSubmissionDTO> getSubmissions(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(assignment.getCourse().getId(), teacherId);
        }
        return submissionRepository.findByAssignment_Id(assignmentId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AssignmentSubmissionDTO gradeSubmission(
            Long assignmentId,
            Long studentId,
            Long teacherId,
            Double score,
            String feedback
    ) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(assignment.getCourse().getId(), teacherId);
        }
        AssignmentSubmission submission = submissionRepository
                .findByAssignment_IdAndStudent_Id(assignmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        submission.setScore(score);
        submission.setFeedback(feedback);
        AssignmentSubmission saved = submissionRepository.save(submission);

        if (saved.getStudent() != null && saved.getStudent().getUser() != null) {
            notificationService.createForUser(
                    saved.getStudent().getUser().getId(),
                    "Assignment Graded",
                    "Your submission for '" + assignment.getTitle() + "' has been graded.",
                    NotificationType.GRADE_POSTED
            );
        }
        return toDto(saved);
    }

    private AssignmentDTO toDto(Assignment assignment) {
        return new AssignmentDTO(
                assignment.getId(),
                assignment.getSubject() != null ? assignment.getSubject().getId() : null,
                assignment.getSubject() != null ? assignment.getSubject().getName() : null,
                assignment.getCourse() != null ? assignment.getCourse().getId() : null,
                assignment.getCourse() != null ? assignment.getCourse().getName() : null,
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getMaxScore(),
                assignment.getCreatedBy() != null ? assignment.getCreatedBy().getId() : null,
                assignment.getCreatedBy() != null ? assignment.getCreatedBy().getName() : null,
                assignment.getCreatedAt()
        );
    }

    private AssignmentSubmissionDTO toDto(AssignmentSubmission submission) {
        return new AssignmentSubmissionDTO(
                submission.getId(),
                submission.getAssignment() != null ? submission.getAssignment().getId() : null,
                submission.getAssignment() != null ? submission.getAssignment().getTitle() : null,
                submission.getStudent() != null ? submission.getStudent().getId() : null,
                submission.getStudent() != null ? submission.getStudent().getName() : null,
                submission.getFileUrl(),
                submission.getSubmittedAt(),
                submission.getScore(),
                submission.getFeedback()
        );
    }
}

