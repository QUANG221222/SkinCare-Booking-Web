package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "skin_therapist")
public class SkinTherapist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "_id", nullable = false, updatable = false)
    private UUID id; //

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 255)
    private String therapistName;

    @Column(length = 255)
    private String displayName;

    @Column(length = 50, nullable = false)
    private String role = "Therapist";

    @ElementCollection
    @CollectionTable(name = "therapist_services_booking", joinColumns = @JoinColumn(name = "therapist_id"))
    @Column(name = "service_booking")
    private Set<String> servicesBooking;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean working = false;


    public void setPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\+?[0-9]{7,15}")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.phoneNumber = phoneNumber;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
