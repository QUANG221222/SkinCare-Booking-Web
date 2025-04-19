package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  // Lấy lịch sử thanh toán theo customer
  Page<Payment> findByAppointmentCustomerId(UUID customerId, Pageable pageable);

    @Query("SELECT p FROM Payment p JOIN p.appointment a WHERE a.customer.id = :customerId " +
           "AND (:status IS NULL OR p.paymentStatus = :status) " +
           "AND (:startDate IS NULL OR p.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR p.createdAt <= :endDate)")
    Page<Payment> findByAppointmentCustomerIdAndFilters(
            @Param("customerId") UUID customerId,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
