package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skin_therapist")
public class Skin_Therapist {

    @Id
    @Column(name = "_id", length = 36, nullable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 255)
    private String therapistName;

    @Column(length = 255)
    private String displayName;

    @Column(length = 50, nullable = false)
    private String role = "Therapist";

    @Lob
    @Column(columnDefinition = "JSON")
    private String servicesBooking;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean working = false;

    // GETTER / SETTER

    public Skin_Therapist() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTherapistName() {
        return therapistName;
    }

    public void setTherapistName(String therapistName) {
        this.therapistName = therapistName;
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

    public String getServicesBooking() {
        return servicesBooking;
    }

    public void setServicesBooking(String servicesBooking) {
        this.servicesBooking = servicesBooking;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }
}