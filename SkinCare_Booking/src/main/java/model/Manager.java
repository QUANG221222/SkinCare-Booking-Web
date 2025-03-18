package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "manager")
public class Manager {

    @Id
    @UuidGenerator
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

    @ElementCollection
    @CollectionTable(name = "manager_staff", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "staff_id")
    @Singular
    private List<String> staffIds;

    @ElementCollection
    @CollectionTable(name = "manager_therapist", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "therapist_id")
    @Singular
    private List<String> therapistIds;

    @ElementCollection
    @CollectionTable(name = "manager_member_account", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "member_account")
    @Singular
    private List<String> memberAccounts;

    @ElementCollection
    @CollectionTable(name = "manager_services", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "service")
    @Singular
    private List<String> services;

    @ElementCollection
    @CollectionTable(name = "manager_services_booking", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "service_booking")
    @Singular("serviceBooking")
    private List<String> servicesBooking;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
