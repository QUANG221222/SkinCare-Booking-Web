package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

public class Customer extends User {

    // Additional customer-specific fields

    @Builder.Default
    private String userType = "CUSTOMER";

    protected void onCreate() {
        super.onCreate();
        if (getRole() == null) {
            setRole(Role.ROLE_CUSTOMER);
        }
    }

    private boolean accountVerified;

    public boolean getAccountVerified() {
        return accountVerified;
    }

    @OneToMany(mappedBy = "customer")
    private Set<SecureToken> tokens;


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore // Tránh vòng lặp khi gọi API
    @OneToMany(mappedBy = "customer")
    private List<Appointment> appointments; // Danh sách lịch hẹn của khách hàng

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "customer")
    private List<Feedback> feedbacks; // Danh sách phản hồi của khách hàng
}

