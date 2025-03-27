package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByCustomer(Customer customer);

}
