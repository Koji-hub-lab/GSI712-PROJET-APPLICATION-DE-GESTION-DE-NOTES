package StudentManagementSystem.controller;

import StudentManagementSystem.entity.Role;
import StudentManagementSystem.entity.User;
import StudentManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SuperAdminController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/super-admin")
    public User createSuperAdmin(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.SUPER_ADMIN);
        return userService.createSuperAdmin(user);
    }
}