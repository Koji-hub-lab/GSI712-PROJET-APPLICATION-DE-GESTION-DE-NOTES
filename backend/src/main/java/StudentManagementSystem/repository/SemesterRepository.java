package StudentManagementSystem.repository;

import StudentManagementSystem.entity.Semester;
import StudentManagementSystem.entity.SemesterStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findByStatus(SemesterStatus status);
}

