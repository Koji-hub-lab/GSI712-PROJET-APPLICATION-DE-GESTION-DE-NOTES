package StudentManagementSystem.repository;

import StudentManagementSystem.entity.CourseSubject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseSubjectRepository extends JpaRepository<CourseSubject, Long> {

    Optional<CourseSubject> findByCourse_IdAndSubject_Id(Long courseId, Long subjectId);

    boolean existsByCourse_IdAndSubject_Id(Long courseId, Long subjectId);

    @EntityGraph(attributePaths = {"course", "subject"})
    List<CourseSubject> findByCourse_Id(Long courseId);

    @EntityGraph(attributePaths = {"course", "subject"})
    List<CourseSubject> findByCourse_IdIn(List<Long> courseIds);
}
