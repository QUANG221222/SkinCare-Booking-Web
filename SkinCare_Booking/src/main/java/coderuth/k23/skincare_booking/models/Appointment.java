package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Appointment_id", nullable = false, unique = true)
    private Long id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Khách hàng đặt lịch

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "skin_therapist_id", nullable = false)
    private SkinTherapist skinTherapist; // Chuyên viên được chỉ định

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "spa_service_id", nullable = false)
    private SpaService spaService; // Dịch vụ được đặt

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    //Cột staff_id trong bảng appointments sẽ chỉ ra quản lý nào giám sát lịch hẹn đó.
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    //Cột manager_id trong bảng appointments sẽ chỉ ra quản lý nào giám sát lịch hẹn đó.
    @JoinColumn(name = "manager_id")
    private Manager manager; // Quản lý giám sát lịch hẹn

    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime; // Thời gian diễn ra lịch hẹn

    @Column(name = "status")
    private String status; // Trạng thái: PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist //sẽ được gọi khi 1 entity được lưu vào csdl đầu tiên.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate //sẽ được gọi ngay trước khi một entity được cập nhật trong cơ sở dữ liệu
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
