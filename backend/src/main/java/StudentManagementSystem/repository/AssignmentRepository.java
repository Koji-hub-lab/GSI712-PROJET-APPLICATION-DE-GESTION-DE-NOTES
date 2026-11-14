package StudentManagementSystem.repository;

import StudentManagementSystem.entity.Assignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @EntityGraph(attributePaths = {"course", "subject", "createdBy"})
    List<Assignment> findByCourse_Id(Long courseId);

    @EntityGraph(attributePaths = {"course", "subject", "createdBy"})
    List<Assignment> findByCreatedBy_Id(Long teacherId);

    @EntityGraph(attributePaths = {"course", "subject", "createdBy"})
    List<Assignment> findByCourse_IdIn(List<Long> courseIds);
}

