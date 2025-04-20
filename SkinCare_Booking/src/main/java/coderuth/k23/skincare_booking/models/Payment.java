package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "amount", nullable = false)
    @Min(value = 0, message = "Số tiền không được âm")
    private Double amount; // Số tiền thanh toán

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Phương thức thanh toán (CASH, CARD, TRANSFER)

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus; // Trạng thái thanh toán

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Thời gian thanh toán


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Column(name = "transaction_id")
    private String transactionId; // Mã giao dịch (dùng cho QR hoặc các phương thức khác)

    public enum PaymentStatus {
        UNPAID,    // Chưa thanh toán
        PAID,      // Đã thanh toán
        REFUNDED   // Đã hoàn tiền
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if(paymentStatus == null) {
            paymentStatus = PaymentStatus.UNPAID; // Mặc định là chưa thanh toán
        }
    }
        
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}