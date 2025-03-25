package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Data
@Table(name = "managers")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)


public class Manager extends User {

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "manager")
    private List<Feedback> feedbacks; // Danh sách phản hồi mà quản lý giám sát

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "manager")
    private List<SkinTherapist> skinTherapists; // Danh sách chuyên viên mà quản lý quản lý

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "manager")
    private List<Appointment> appointments; // Danh sách lịch hẹn mà quản lý giám sát

//    @OneToMany(mappedBy = "manager")
//    private List<Customer> customers; //Danh sách khách hàng mà quản lý hỗ trợ
}