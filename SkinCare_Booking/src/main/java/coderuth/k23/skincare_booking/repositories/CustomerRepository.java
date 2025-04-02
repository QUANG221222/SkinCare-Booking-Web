package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.SpaService;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CustomerRepository extends UserBaseRepository<Customer> {
    // Add specific customer methods if needed
}
