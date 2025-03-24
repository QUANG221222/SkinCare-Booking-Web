package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "skin_therapists")
@SuperBuilder



public class SkinTherapist extends User {

    @Column(name = "specialization")
    private String specialization;

    @OneToMany(mappedBy = "skinTherapist")
    private List<Appointment> appointments; // Danh sách lịch hẹn của chuyên viên

    @OneToMany(mappedBy = "skinTherapist")
    private List<Feedback> feedbacks; // Danh sách phản hồi liên quan đến chuyên viên

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager; // Quản lý phụ trách chuyên viên
    //Cột manager_id trong bảng skin_therapists sẽ chỉ ra quản lý nào phụ trách chuyên viên đó.



    // Nếu cần thêm logic, bạn có thể viết ở đây
}