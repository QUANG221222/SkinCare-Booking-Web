package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;


@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Staff extends User {

    @OneToMany(mappedBy = "staff")
    private List<Appointment> appointments; // Danh sách lịch hẹn mà nhân viên quản lý

}
