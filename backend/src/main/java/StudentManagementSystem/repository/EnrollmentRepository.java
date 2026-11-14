package StudentManagementSystem.repository;

import StudentManagementSystem.entity.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudent_IdAndSubject_Id(Long studentId, Long subjectId);

    boolean existsByStudent_IdAndCourse_IdAndSubject_Id(Long studentId, Long courseId, Long subjectId);

    @EntityGraph(attributePaths = {"course", "subject", "student"})
    List<Enrollment> findByStudent_IdAndCourse_IdIn(Long studentId, List<Long> courseIds);

    @EntityGraph(attributePaths = {"subject", "course", "student"})
    List<Enrollment> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    @EntityGraph(attributePaths = {"course", "subject", "student"})
    List<Enrollment> findByCourse_IdAndStudent_IdIn(Long courseId, List<Long> studentIds);

    long countByStudent_Id(Long studentId);
}
