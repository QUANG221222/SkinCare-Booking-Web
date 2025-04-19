package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  // Lấy lịch sử thanh toán theo customer
  List<Payment> findByAppointmentCustomerId(UUID customerId);

  // Lấy lịch sử thanh toán theo appointment
  List<Payment> findByAppointmentId(Long appointmentId);

  List<Payment> findByCreatedAtBetweenAndPaymentStatus(LocalDateTime start, LocalDateTime end, Payment.PaymentStatus status);
}
