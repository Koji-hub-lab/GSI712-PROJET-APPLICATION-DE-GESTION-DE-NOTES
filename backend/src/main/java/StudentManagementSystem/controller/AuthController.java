package StudentManagementSystem.controller;


import StudentManagementSystem.dto.ApiResponse;
import StudentManagementSystem.entity.Role;
import StudentManagementSystem.entity.User;
import StudentManagementSystem.repository.StudentRepository;
import StudentManagementSystem.repository.TeacherRepository;
import StudentManagementSystem.repository.UserRepository;
import StudentManagementSystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            if (!userRepository.existsByRole(Role.SUPER_ADMIN)) {
                User admin = new User();
                admin.setEmail(email);
                admin.setUsername(email);
                admin.setName("Super Admin");
                admin.setRole(Role.SUPER_ADMIN);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setActive(true);
                userRepository.save(admin);
                user = admin;
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("role", user.getRole().name());
        if (user.getRole() == Role.STUDENT) {
            studentRepository.findByUser_Id(user.getId()).ifPresent(s -> userMap.put("studentId", s.getId()));
        }
        if (user.getRole() == Role.TEACHER) {
            teacherRepository.findByUser_Id(user.getId()).ifPresent(t -> userMap.put("teacherId", t.getId()));
        }

        return ApiResponse.ok(Map.of(
                "token", token,
                "user", userMap
        ));
    }

    @PostMapping("/bootstrap")
    public ApiResponse<?> bootstrap(@RequestBody(required = false) Map<String, String> request) {
        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            return ApiResponse.error("Super admin already exists");
        }
        String email = request != null && request.get("email") != null ? request.get("email") : "admin@example.com";
        String name = request != null && request.get("name") != null ? request.get("name") : "Super Admin";
        String password = request != null && request.get("password") != null ? request.get("password") : "Admin@123";

        User admin = new User();
        admin.setEmail(email);
        admin.setName(name);
        admin.setUsername(email); // satisfy legacy username column
        admin.setRole(Role.SUPER_ADMIN);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setActive(true);
        userRepository.save(admin);

        String token = jwtUtil.generateToken(admin);

         return ApiResponse.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", admin.getId(),
                        "name", admin.getName(),
                        "email", admin.getEmail(),
                        "role", admin.getRole().name()
                )
        ));
    }
}
