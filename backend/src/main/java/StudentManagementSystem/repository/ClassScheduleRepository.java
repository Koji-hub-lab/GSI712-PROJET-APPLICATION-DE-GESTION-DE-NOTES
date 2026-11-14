package StudentManagementSystem.repository;

import StudentManagementSystem.entity.ClassSchedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    @EntityGraph(attributePaths = {"course", "subject", "teacher"})
    List<ClassSchedule> findByCourse_Id(Long courseId);

    @EntityGraph(attributePaths = {"course", "subject", "teacher"})
    List<ClassSchedule> findByTeacher_Id(Long teacherId);

    @EntityGraph(attributePaths = {"course", "subject", "teacher"})
    List<ClassSchedule> findByCourse_IdIn(List<Long> courseIds);
}

