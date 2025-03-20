package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)

    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "manager_name", nullable = false, unique = true)
    private String managerName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "role", nullable = false)
    private String role = "Manager";

    @ElementCollection
    @CollectionTable(name = "manager_staff")
    @Column(name = "staff_id")
    private List<String> staff = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "manager_therapist")
    @Column(name = "therapist_id")
    private List<String> therapistId = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "manager_member_account")
    @Column(name = "member_account_id")
    private List<String> memberAccountId = new ArrayList<>();

//    @ElementCollection
//    @CollectionTable(name = "manager_services")
//    @Column(name = "services_id")
//    private List<String> services = new ArrayList<>();

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "manager_id")
//    private List<ServiceBooking> servicesBooking = new ArrayList<>();

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updateAt;

    // Default Constructor
    public Manager() {
    }

    // Constructor with required fields
    public Manager(String email, String password, String managerName) {
        this.email = email;
        this.password = password;
        this.managerName = managerName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getStaff() {
        return staff;
    }

    public void setStaff(List<String> staff) {
        this.staff = staff;
    }

    public List<String> getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(List<String> therapistId) {
        this.therapistId = therapistId;
    }

    public List<String> getMemberAccountId() {
        return memberAccountId;
    }

    public void setMemberAccountId(List<String> memberAccountId) {
        this.memberAccountId = memberAccountId;
    }

//    public List<String> getServices() {
//        return services;
//    }
//
//    public void setServices(List<String> services) {
//        this.services = services;
//    }
//
//    public List<ServiceBooking> getServicesBooking() {
//        return servicesBooking;
//    }
//
//    public void setServicesBooking(List<ServiceBooking> servicesBooking) {
//        this.servicesBooking = servicesBooking;
//    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}