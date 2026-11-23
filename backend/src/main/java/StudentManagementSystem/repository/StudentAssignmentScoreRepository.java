package StudentManagementSystem.repository;

import StudentManagementSystem.entity.StudentAssignmentScore;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAssignmentScoreRepository extends JpaRepository<StudentAssignmentScore, Long> {

    @EntityGraph(attributePaths = {"student", "assignment", "assignment.course", "assignment.subject", "gradedBy"})
    List<StudentAssignmentScore> findByStudent_Id(Long studentId);

    @EntityGraph(attributePaths = {"student", "assignment", "assignment.course", "assignment.subject", "gradedBy"})
    List<StudentAssignmentScore> findByAssignment_Id(Long assignmentId);

    @EntityGraph(attributePaths = {"student", "assignment", "assignment.course", "assignment.subject", "gradedBy"})
    Optional<StudentAssignmentScore> findByStudent_IdAndAssignment_Id(Long studentId, Long assignmentId);
}
