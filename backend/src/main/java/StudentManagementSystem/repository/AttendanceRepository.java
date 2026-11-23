package StudentManagementSystem.repository;

import StudentManagementSystem.entity.Attendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @EntityGraph(attributePaths = {"student", "subject", "course", "recordedBy"})
    List<Attendance> findByStudent_Id(Long studentId);

    @EntityGraph(attributePaths = {"student", "subject", "course", "recordedBy"})
    List<Attendance> findByCourse_Id(Long courseId);

    @EntityGraph(attributePaths = {"student", "subject", "course", "recordedBy"})
    List<Attendance> findByRecordedBy_Id(Long teacherId);

    @EntityGraph(attributePaths = {"student", "subject", "course", "recordedBy"})
    List<Attendance> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}

