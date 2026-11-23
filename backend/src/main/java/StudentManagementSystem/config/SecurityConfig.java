package StudentManagementSystem.config;

import StudentManagementSystem.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> { })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/auth/bootstrap").permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/teacher/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        .requestMatchers("/student/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/assignments/create").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/courses/*/subjects/*/assignments").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/courses/*/subjects/*/grades").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.POST, "/assignments/*/grade").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/assignments/*/scores").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.POST, "/subjects/*/students/*/grade").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        // Allow students to create their own enrollments; other enrollment ops remain teacher/super-admin
                        .requestMatchers(HttpMethod.POST, "/enrollment/add").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers("/course/**", "/subject/**", "/enrollment/**", "/grade/**")
                        .hasAnyAuthority("SUPER_ADMIN", "ADMIN", "TEACHER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
