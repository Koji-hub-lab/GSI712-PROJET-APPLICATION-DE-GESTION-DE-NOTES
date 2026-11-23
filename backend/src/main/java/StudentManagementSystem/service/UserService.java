package StudentManagementSystem.service;


import StudentManagementSystem.entity.Role;
import StudentManagementSystem.entity.User;
import StudentManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

      public User createSuperAdmin(User user) {
        if(user.getRole() == Role.SUPER_ADMIN && userRepository.existsByRole(Role.SUPER_ADMIN)) {
            throw new RuntimeException("Super Admin already exists!");
        }
        return userRepository.save(user);
    }
}
