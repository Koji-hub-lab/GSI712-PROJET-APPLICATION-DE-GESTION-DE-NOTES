package StudentManagementSystem.repository;

import StudentManagementSystem.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {}