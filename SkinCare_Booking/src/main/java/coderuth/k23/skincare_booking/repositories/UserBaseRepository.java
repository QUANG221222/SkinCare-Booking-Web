package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface UserBaseRepository<T extends User> extends JpaRepository<T, UUID> {
    Optional<T> findByUsername(String username);
    Optional<T> findByEmail(String email);
    Optional<T> findByUsernameAndEmail(String username, String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
