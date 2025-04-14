package coderuth.k23.skincare_booking.models;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass // Class User này không tạo bảng, chỉ để các class con kế thừa
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    public enum Role {
        ROLE_CUSTOMER,
        ROLE_MANAGER,
        ROLE_STAFF,
        ROLE_THERAPIST
    }


    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    private String userType;

    @PrePersist // sẽ được gọi khi 1 entity được lưu vào csdl đầu tiên.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate //sẽ được gọi ngay trước khi một entity được cập nhật trong cơ sở dữ liệu
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

