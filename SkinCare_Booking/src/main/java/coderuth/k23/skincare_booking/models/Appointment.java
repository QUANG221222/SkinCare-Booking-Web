package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Khách hàng đặt lịch

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "skin_therapist_id", nullable = true)
    private SkinTherapist skinTherapist; // Chuyên viên được chỉ định

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime appointmentTime; // Thời gian diễn ra lịch hẹn

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AppointmentStatus status; // Trạng thái đặt dịch vụ

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    private String result; // Kết quả thực hiện dịch vụ (do chuyên viên ghi nhận)

    public enum AppointmentStatus {
        PENDING,        // Đang chờ xử lý (mới đặt)
        CHECKED_IN,     // Đã check-in tại trung tâm
        ASSIGNED,       // Đã phân công chuyên viên
        COMPLETED,      // Chuyên viên đã hoàn thành dịch vụ
        CHECKED_OUT,    // Đã check-out
        CANCELLED       // Đã hủy
    }

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @PrePersist //sẽ được gọi khi 1 entity được lưu vào csdl đầu tiên.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = AppointmentStatus.PENDING; // Mặc định trạng thái PENDING
        }
    }

    @PreUpdate //sẽ được gọi ngay trước khi một entity được cập nhật trong cơ sở dữ liệu
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
