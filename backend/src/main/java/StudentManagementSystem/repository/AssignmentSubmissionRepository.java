package StudentManagementSystem.repository;

import StudentManagementSystem.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    @EntityGraph(attributePaths = {"assignment", "assignment.course", "assignment.subject", "student"})
    List<AssignmentSubmission> findByAssignment_Id(Long assignmentId);

    @EntityGraph(attributePaths = {"assignment", "assignment.course", "assignment.subject", "student"})
    List<AssignmentSubmission> findByStudent_Id(Long studentId);

    @EntityGraph(attributePaths = {"assignment", "assignment.course", "assignment.subject", "student"})
    Optional<AssignmentSubmission> findByAssignment_IdAndStudent_Id(Long assignmentId, Long studentId);
}

