package StudentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Legacy databases may still have a 'username' column; keep it populated alongside name/email
    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String username;
    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    public void ensureUsername() {
        if (username == null || username.isBlank()) {
            this.username = this.email;
        }
        if (active == null) {
            this.active = true;
        }
    }

    @Column(nullable = false)
    private Boolean active = true;
}
