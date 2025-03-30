package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "managers")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)


public class Manager extends User {

    @Builder.Default
    private String userType = "MANAGER";

    protected void onCreate() {
        super.onCreate();
        if (getRole() == null) {
            setRole(Role.ROLE_MANAGER);
        }
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "manager")
    private List<Feedback> feedbacks; // Danh sách phản hồi mà quản lý giám sát

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore // Tránh vòng lặp khi gọi API
    @OneToMany(mappedBy = "manager")
    private List<SkinTherapist> skinTherapists; // Danh sách chuyên viên mà quản lý quản lý

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore // Tránh vòng lặp khi gọi API
    @OneToMany(mappedBy = "manager")
    private List<Appointment> appointments; // Danh sách lịch hẹn mà quản lý giám sát

//    @OneToMany(mappedBy = "manager")
//    private List<Customer> customers; //Danh sách khách hàng mà quản lý hỗ trợ
}