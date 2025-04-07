package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  List<Appointment> findByCustomerId(UUID customerId);
  List<Appointment> findBySkinTherapistId(UUID skinTherapistId);
  List<Appointment> findByStatus(Appointment.AppointmentStatus status);
}
