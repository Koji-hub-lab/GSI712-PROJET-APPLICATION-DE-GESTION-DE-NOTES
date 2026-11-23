package StudentManagementSystem.service;

import StudentManagementSystem.dto.AssignmentDTO;
import StudentManagementSystem.dto.StudentAssignmentScoreDTO;
import StudentManagementSystem.dto.StudentSubjectGradeDTO;
import StudentManagementSystem.entity.Assignment;
import StudentManagementSystem.entity.Student;
import StudentManagementSystem.entity.StudentAssignmentScore;
import StudentManagementSystem.entity.StudentSubjectGrade;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.entity.Teacher;
import StudentManagementSystem.exception.ResourceNotFoundException;
import StudentManagementSystem.repository.AssignmentRepository;
import StudentManagementSystem.repository.CourseRepository;
import StudentManagementSystem.repository.CourseStudentRepository;
import StudentManagementSystem.repository.CourseSubjectRepository;
import StudentManagementSystem.repository.EnrollmentRepository;
import StudentManagementSystem.repository.StudentAssignmentScoreRepository;
import StudentManagementSystem.repository.StudentRepository;
import StudentManagementSystem.repository.StudentSubjectGradeRepository;
import StudentManagementSystem.repository.SubjectRepository;
import StudentManagementSystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentGradingService {

    private static final Set<String> VALID_GRADES = Set.of("A", "B", "C", "D", "F");

    private final AssignmentService assignmentService;
    private final AssignmentRepository assignmentRepository;
    private final StudentAssignmentScoreRepository assignmentScoreRepository;
    private final StudentSubjectGradeRepository subjectGradeRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;

    @Transactional
    public AssignmentDTO createAssignment(AssignmentDTO dto) {
        return assignmentService.createAssignment(dto);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAssignmentsByCourseAndSubject(Long courseId, Long subjectId, Long teacherId) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found");
        }
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found");
        }
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(courseId, subjectId)) {
            throw new IllegalArgumentException("Subject does not belong to this course.");
        }
        return assignmentRepository.findByCourse_Id(courseId).stream()
                .filter(a -> a.getSubject() != null && subjectId.equals(a.getSubject().getId()))
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional
    public StudentAssignmentScoreDTO gradeAssignment(
            Long assignmentId,
            Long studentId,
            Long teacherId,
            Double score,
            String feedback
    ) {
        if (studentId == null || teacherId == null || score == null) {
            throw new IllegalArgumentException("studentId, teacherId and score are required.");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        Long courseId = assignment.getCourse() != null ? assignment.getCourse().getId() : null;
        Long subjectId = assignment.getSubject() != null ? assignment.getSubject().getId() : null;
        if (courseId == null || subjectId == null) {
            throw new IllegalStateException("Assignment is missing course/subject linkage.");
        }

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        courseService.ensureTeacherOwnsCourse(courseId, teacher.getId());
        validateStudentEnrollment(student.getId(), courseId, subjectId);

        if (assignment.getMaxScore() != null && score > assignment.getMaxScore()) {
            throw new IllegalArgumentException("Score cannot exceed assignment maxScore.");
        }
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative.");
        }

        StudentAssignmentScore grade = assignmentScoreRepository
                .findByStudent_IdAndAssignment_Id(studentId, assignmentId)
                .orElse(new StudentAssignmentScore());

        grade.setStudent(student);
        grade.setAssignment(assignment);
        grade.setScore(score);
        grade.setFeedback(feedback);
        grade.setGradedBy(teacher);
        grade.setGradedAt(LocalDateTime.now());

        return toAssignmentScoreDto(assignmentScoreRepository.save(grade));
    }

    @Transactional
    public StudentSubjectGradeDTO gradeSubject(
            Long subjectId,
            Long studentId,
            Long courseId,
            Long teacherId,
            Double totalScore,
            String gradeValue
    ) {
        if (subjectId == null || studentId == null || courseId == null || teacherId == null || totalScore == null) {
            throw new IllegalArgumentException("subjectId, studentId, courseId, teacherId and totalScore are required.");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found");
        }

        courseService.ensureTeacherOwnsCourse(courseId, teacher.getId());
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(courseId, subjectId)) {
            throw new IllegalArgumentException("Subject does not belong to this course.");
        }
        validateStudentEnrollment(student.getId(), courseId, subjectId);

        String normalizedGrade = normalizeGrade(gradeValue);
        if (totalScore < 0) {
            throw new IllegalArgumentException("totalScore cannot be negative.");
        }

        StudentSubjectGrade subjectGrade = subjectGradeRepository
                .findByStudent_IdAndSubject_Id(studentId, subjectId)
                .orElse(new StudentSubjectGrade());

        subjectGrade.setStudent(student);
        subjectGrade.setSubject(subject);
        subjectGrade.setCourse(courseRepository.getReferenceById(courseId));
        subjectGrade.setTotalScore(totalScore);
        subjectGrade.setGrade(normalizedGrade);
        subjectGrade.setGradedBy(teacher);
        subjectGrade.setGradedAt(LocalDateTime.now());

        return toSubjectGradeDto(subjectGradeRepository.save(subjectGrade));
    }

    @Transactional(readOnly = true)
    public List<StudentSubjectGradeDTO> getStudentSubjectGrades(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found");
        }
        return subjectGradeRepository.findByStudent_Id(studentId).stream()
                .map(this::toSubjectGradeDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentAssignmentScoreDTO> getStudentAssignmentScores(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found");
        }
        return assignmentScoreRepository.findByStudent_Id(studentId).stream()
                .map(this::toAssignmentScoreDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentAssignmentScoreDTO> getAssignmentScores(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        Long courseId = assignment.getCourse() != null ? assignment.getCourse().getId() : null;
        if (courseId == null) {
            throw new IllegalStateException("Assignment is missing course linkage.");
        }
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        return assignmentScoreRepository.findByAssignment_Id(assignmentId).stream()
                .map(this::toAssignmentScoreDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentSubjectGradeDTO> getCourseSubjectGrades(Long courseId, Long subjectId, Long teacherId) {
        if (teacherId != null) {
            courseService.ensureTeacherOwnsCourse(courseId, teacherId);
        }
        if (!courseSubjectRepository.existsByCourse_IdAndSubject_Id(courseId, subjectId)) {
            throw new IllegalArgumentException("Subject does not belong to this course.");
        }
        return subjectGradeRepository.findByCourse_IdAndSubject_Id(courseId, subjectId).stream()
                .map(this::toSubjectGradeDto)
                .toList();
    }

    private void validateStudentEnrollment(Long studentId, Long courseId, Long subjectId) {
        if (!courseStudentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId)) {
            throw new IllegalArgumentException("Student is not enrolled in this course.");
        }
        if (!enrollmentRepository.existsByStudent_IdAndCourse_IdAndSubject_Id(studentId, courseId, subjectId)) {
            throw new IllegalArgumentException("Student is not enrolled in this subject.");
        }
    }

    private String normalizeGrade(String grade) {
        if (grade == null || grade.isBlank()) {
            throw new IllegalArgumentException("grade is required.");
        }
        String normalized = grade.trim().toUpperCase();
        if (!VALID_GRADES.contains(normalized)) {
            throw new IllegalArgumentException("grade must be one of A, B, C, D, F.");
        }
        return normalized;
    }

    private AssignmentDTO toAssignmentDto(Assignment assignment) {
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

    private StudentAssignmentScoreDTO toAssignmentScoreDto(StudentAssignmentScore score) {
        Assignment assignment = score.getAssignment();
        return new StudentAssignmentScoreDTO(
                score.getId(),
                score.getStudent() != null ? score.getStudent().getId() : null,
                score.getStudent() != null ? score.getStudent().getName() : null,
                assignment != null ? assignment.getId() : null,
                assignment != null ? assignment.getTitle() : null,
                assignment != null && assignment.getCourse() != null ? assignment.getCourse().getId() : null,
                assignment != null && assignment.getCourse() != null ? assignment.getCourse().getName() : null,
                assignment != null && assignment.getSubject() != null ? assignment.getSubject().getId() : null,
                assignment != null && assignment.getSubject() != null ? assignment.getSubject().getName() : null,
                score.getScore(),
                assignment != null ? assignment.getMaxScore() : null,
                score.getGradedBy() != null ? score.getGradedBy().getId() : null,
                score.getGradedBy() != null ? score.getGradedBy().getName() : null,
                score.getGradedAt(),
                score.getFeedback()
        );
    }

    private StudentSubjectGradeDTO toSubjectGradeDto(StudentSubjectGrade grade) {
        return new StudentSubjectGradeDTO(
                grade.getId(),
                grade.getStudent() != null ? grade.getStudent().getId() : null,
                grade.getStudent() != null ? grade.getStudent().getName() : null,
                grade.getSubject() != null ? grade.getSubject().getId() : null,
                grade.getSubject() != null ? grade.getSubject().getName() : null,
                grade.getCourse() != null ? grade.getCourse().getId() : null,
                grade.getCourse() != null ? grade.getCourse().getName() : null,
                grade.getTotalScore(),
                grade.getGrade(),
                grade.getGradedBy() != null ? grade.getGradedBy().getId() : null,
                grade.getGradedBy() != null ? grade.getGradedBy().getName() : null,
                grade.getGradedAt()
        );
    }
}
