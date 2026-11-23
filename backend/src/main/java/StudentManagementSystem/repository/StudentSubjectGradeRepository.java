package StudentManagementSystem.repository;

import StudentManagementSystem.entity.StudentSubjectGrade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSubjectGradeRepository extends JpaRepository<StudentSubjectGrade, Long> {

    @EntityGraph(attributePaths = {"student", "subject", "course", "gradedBy"})
    List<StudentSubjectGrade> findByStudent_Id(Long studentId);

    @EntityGraph(attributePaths = {"student", "subject", "course", "gradedBy"})
    Optional<StudentSubjectGrade> findByStudent_IdAndSubject_Id(Long studentId, Long subjectId);

    @EntityGraph(attributePaths = {"student", "subject", "course", "gradedBy"})
    List<StudentSubjectGrade> findByCourse_IdAndSubject_Id(Long courseId, Long subjectId);
}
