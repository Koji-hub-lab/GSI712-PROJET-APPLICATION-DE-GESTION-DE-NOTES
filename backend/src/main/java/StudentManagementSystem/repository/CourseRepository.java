package StudentManagementSystem.repository;

import StudentManagementSystem.entity.Course;
import StudentManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
   }
