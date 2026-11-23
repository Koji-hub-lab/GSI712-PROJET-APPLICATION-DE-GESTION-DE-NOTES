package StudentManagementSystem.repository;

import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.Subject;
import StudentManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject,Long> {

}
