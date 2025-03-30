package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Manager;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends UserBaseRepository<Manager> {
    // Add specific manager methods if needed
}
