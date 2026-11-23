package StudentManagementSystem.repository;

import StudentManagementSystem.entity.CourseStudent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {

    @Override
    @EntityGraph(attributePaths = {"course", "course.teacher", "student"})
    List<CourseStudent> findAll();

    Optional<CourseStudent> findByCourse_IdAndStudent_Id(Long courseId, Long studentId);

    boolean existsByCourse_IdAndStudent_Id(Long courseId, Long studentId);

    @EntityGraph(attributePaths = {"course", "course.teacher", "student"})
    List<CourseStudent> findByStudent_Id(Long studentId);

    @EntityGraph(attributePaths = {"course", "student"})
    List<CourseStudent> findByCourse_Id(Long courseId);

    @EntityGraph(attributePaths = {"course", "course.teacher", "student"})
    List<CourseStudent> findByCourse_IdIn(List<Long> courseIds);

    long countByCourse_Id(Long courseId);
}
