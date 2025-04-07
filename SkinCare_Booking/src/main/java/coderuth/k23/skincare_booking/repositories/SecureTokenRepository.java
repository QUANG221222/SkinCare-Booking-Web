package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SecureTokenRepository extends JpaRepository<SecureToken, UUID> {
    SecureToken findByToken(final String token);

    // Truy vấn các token đã hết hạn
    List<SecureToken> findByExpiredAtBefore(LocalDateTime time);

}