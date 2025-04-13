package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Staff extends User {

    @Builder.Default
    private String userType = "STAFF";

    protected void onCreate() {
        super.onCreate();
        if (getRole() == null) {
            setRole(Role.ROLE_STAFF);
        }
    }

    @Column(name = "img", nullable = false)
    private String img;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore // Tránh vòng lặp khi gọi API
    @OneToMany(mappedBy = "staff")
    private List<Appointment> appointments; // Danh sách lịch hẹn mà nhân viên quản lý
}

