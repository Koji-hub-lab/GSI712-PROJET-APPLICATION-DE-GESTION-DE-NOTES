package StudentManagementSystem.repository;

import StudentManagementSystem.entity.CourseCompletion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseCompletionRepository extends JpaRepository<CourseCompletion, Long> {

    @EntityGraph(attributePaths = {"student", "course"})
    List<CourseCompletion> findByStudent_Id(Long studentId);

    @EntityGraph(attributePaths = {"student", "course"})
    Optional<CourseCompletion> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}

