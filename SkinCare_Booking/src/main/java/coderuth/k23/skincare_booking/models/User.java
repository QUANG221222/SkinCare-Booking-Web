package coderuth.k23.skincare_booking.models;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass // Class User này không tạo bảng, chỉ để các class con kế thừa
@Data //sinh ra getter,setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "location")
    private String location;

    @Column(name = "role")
    private String role;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "role")
//    public Role role;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist // sẽ được gọi khi 1 entity được lưu vào csdl đầu tiên.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate //sẽ được gọi ngay trước khi một entity được cập nhật trong cơ sở dữ liệu
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}