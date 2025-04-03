package coderuth.k23.skincare_booking.repositories;


import coderuth.k23.skincare_booking.models.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SecureTokenRepository extends JpaRepository<SecureToken, UUID> {

    SecureToken findByToken(final String token);
    UUID removeByToken(final String token);
}