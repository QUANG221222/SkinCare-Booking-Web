package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "staff")
    public class Staff {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false, unique = true)
        private String id;

        @Column(name = "email", nullable = false, unique = true)
        private String email;

        @Column(name = "password", nullable = false)
        private String password;

        @Column(name = "staff_name", nullable = false, unique = true)
        private String staffName;

        @Column(name = "display_name", nullable = true)
        private String displayName;

        @Column(name = "role")
        private String role = "Staff";

        @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MemberAccount> member = new ArrayList<>();

//    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Therapist> therapist = new ArrayList<>();
//
//    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SkinTherapistWorking> services = new ArrayList<>();

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = true)
        private LocalDateTime updatedAt;


        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = createdAt; // Ban đầu updatedAt bằng createdAt
        }


        @Column(name = "fire", nullable = false)
        private Boolean fire = false;

        // Constructors
        public Staff() {}

        public Staff(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getStaffName() { return staffName; }
        public void setStaffName(String staffName) { this.staffName = staffName; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public List<MemberAccount> getMember() { return member; }
        public void setMember(List<MemberAccount> member) { this.member = member; }

//    public List<Therapist> getTherapist() { return therapist; }
//    public void setTherapist(List<Therapist> therapist) { this.therapist = therapist; }
//
//    public List<SkinTherapistWorking> getServices() { return services; }
//    public void setServices(List<SkinTherapistWorking> services) { this.services = services; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdateAt() { return updatedAt; }
        public void setUpdateAt(LocalDateTime updateAt) { this.updatedAt = updateAt; }

        public Boolean getFire() { return fire; }
        public void setFire(Boolean fire) { this.fire = fire; }}

