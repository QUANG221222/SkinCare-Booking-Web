package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "manager")
public class Manager {

    @Id
    @Column(name = "_id", length = 36, nullable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 255)
    private String managerName;

    @Column(length = 255)
    private String displayName;

    @Column(length = 50, nullable = false)
    private String role = "Manager";

    @Lob
    @Column(columnDefinition = "JSON")
    private String staffId;

    @Lob
    @Column(columnDefinition = "JSON")
    private String therapistId;

    @Lob
    @Column(columnDefinition = "JSON")
    private String memberAccount;

    @Lob
    @Column(columnDefinition = "JSON")
    private String services;

    @Lob
    @Column(columnDefinition = "JSON")
    private String servicesBooking;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    // GETTERS AND SETTERS

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

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(String therapistId) {
        this.therapistId = therapistId;
    }

    public String getMemberAccount() {
        return memberAccount;
    }

    public void setMemberAccount(String memberAccount) {
        this.memberAccount = memberAccount;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
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

    public Manager() {
    }
}