package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends UserBaseRepository<Customer> {
    // Add specific customer methods if needed
}
