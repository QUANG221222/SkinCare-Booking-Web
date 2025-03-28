package coderuth.k23.skincare_booking.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import coderuth.k23.skincare_booking.models.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByUsernameAndEmail(String username, String email);

    boolean existsByUsername(String username);
    boolean existsByEmail (String email);
    boolean existsByPhone (String phone);
}
