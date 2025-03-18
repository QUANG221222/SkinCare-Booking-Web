package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "manager")
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "_id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 255)
    private String managerName;

    @Column(length = 255)
    private String displayName;

    @Column(length = 50, nullable = false)
    private String role = "Manager";

    // Quan hệ One-to-Many với SkinTherapist
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkinTherapist> therapists;

    @ElementCollection
    @CollectionTable(name = "manager_staff", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "staff_id")
    private List<String> staffIds;

    @ElementCollection
    @CollectionTable(name = "manager_member_account", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "member_account")
    private List<String> memberAccounts;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Services> services;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Manager(String email, String password, String managerName, String displayName) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.managerName = managerName;
        this.displayName = displayName;
    }
}