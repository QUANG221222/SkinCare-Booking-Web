package coderuth.k23.skincare_booking.repositories;

import java.util.Optional;
import java.util.UUID;

import coderuth.k23.skincare_booking.models.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail (String email);
}
