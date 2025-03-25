package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Feedback_id", nullable = false, unique = true)
    private Long Id;

    @Column(name = "rating", nullable = false)
    private byte rating; // Điểm đánh giá (ví dụ: 1-5)

    @Column(name = "comment")
    private String comment; // Nội dung phản hồi

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Khách hàng để lại feedback

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "spa_service_id", nullable = false)
    private SpaService spaService; // Dịch vụ được đánh giá

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    //Cột Skin_id trong bảng feedbacks chỉ ra chuyên viên được đánh giá.
    @JoinColumn(name = "skin_therapist_id")
    private SkinTherapist skinTherapist; // Chuyên viên được đánh giá (có thể null nếu không đánh giá chuyên viên)

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    //Cột manager_id trong bảng feedbacks sẽ chỉ ra quản lý nào phụ trách phản hồi đó.
    @JoinColumn(name = "manager_id")
    private Manager manager; // Quản lý giám sát phản hồi

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist // sẽ được gọi khi 1 entity được lưu vào csdl đầu tiên.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate //sẽ được gọi ngay trước khi một entity được cập nhật trong cơ sở dữ liệu
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}