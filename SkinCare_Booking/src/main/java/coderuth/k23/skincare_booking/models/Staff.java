package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "staff_name", nullable = false, unique = true)
    private String staffName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "role", nullable = false)
    private String role = "Staff";

    @ElementCollection
    @CollectionTable(name = "staff_members")
    @Column(name = "member_id")
    private List<String> member = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "staff_therapists")
    @Column(name = "therapist_id")
    private List<String> therapist = new ArrayList<>();

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "staff_id")
//    private List<ServiceBooking> servicesBooking = new ArrayList<>();

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updateAt;

    @Column(name = "fire", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean fire = false;

    // Default Constructor
    public Staff() {
    }

    // Constructor with required fields
    public Staff(String email, String password, String staffName) {
        this.email = email;
        this.password = password;
        this.staffName = staffName;
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

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
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

    public List<String> getMember() {
        return member;
    }

    public void setMember(List<String> member) {
        this.member = member;
    }

    public List<String> getTherapist() {
        return therapist;
    }

    public void setTherapist(List<String> therapist) {
        this.therapist = therapist;
    }

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

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }
}