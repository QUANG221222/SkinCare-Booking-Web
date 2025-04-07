package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.TherapistSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TherapistScheduleRepository extends JpaRepository<TherapistSchedule, UUID> {
    List<TherapistSchedule> findBySkinTherapistId(UUID skinTherapistId);
}
