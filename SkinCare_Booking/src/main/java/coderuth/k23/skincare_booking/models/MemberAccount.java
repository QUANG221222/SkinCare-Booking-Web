package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_account")
public class MemberAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "display_name", nullable = true)
    private String displayName;

    @Column(name = "role")
    private String role = "Member";

    //CascadeType.ALL nghĩa là tất cả các thao tác (persist, merge, remove, refresh, detach) trên MemberAccount sẽ được áp dụng cho tất cả các ServicesBooking liên kết với nó
//    @OneToMany(mappedBy = "memberAccount", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ServicesBooking> servicesBooking = new ArrayList<>();

    //updatable = false: Chỉ định rằng giá trị của cột created_at không thể bị cập nhật sau khi được inserted vào cơ sở dữ liệu. Điều này đảm bảo rằng thời gian tạo tài khoản (ban đầu) không thay đổi, ngay cả khi thực thể được cập nhật
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt; // Ban đầu updatedAt bằng createdAt
    }

    // Constructors
    public MemberAccount() {}

    public MemberAccount(String email, String password, String phone, String username) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.username = username;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

//    public List<ServicesBooking> getServicesBooking() { return servicesBooking; }
//    public void setServicesBooking(List<ServicesBooking> servicesBooking) { this.servicesBooking = servicesBooking; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdateAt() { return updatedAt; }
    public void setUpdateAt(LocalDateTime updateAt) { this.updatedAt = updatedAt; }}