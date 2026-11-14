package StudentManagementSystem.repository;


import StudentManagementSystem.entity.Role;
import StudentManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByRole(Role role);
    User findByEmail(String email);
    java.util.List<User> findAllByRole(Role role);
    boolean existsByEmail(String email);
}
