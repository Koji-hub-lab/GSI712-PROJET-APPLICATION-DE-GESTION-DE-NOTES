package StudentManagementSystem.repository;

import StudentManagementSystem.entity.CourseMaterial;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {

    @EntityGraph(attributePaths = {"course", "subject", "uploadedBy"})
    List<CourseMaterial> findByCourse_Id(Long courseId);

    @EntityGraph(attributePaths = {"course", "subject", "uploadedBy"})
    List<CourseMaterial> findByUploadedBy_Id(Long teacherId);

    @EntityGraph(attributePaths = {"course", "subject", "uploadedBy"})
    List<CourseMaterial> findByCourse_IdIn(List<Long> courseIds);
}

