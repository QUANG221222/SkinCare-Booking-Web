package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserId(String userId);

    void deleteByUserId(String userId);

    void deleteByToken(String token);

    void deleteByUsed(boolean used);
}
