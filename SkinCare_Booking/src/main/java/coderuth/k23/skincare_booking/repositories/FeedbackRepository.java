package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Tìm tất cả Feedback của một Customer và chưa bị ẩn
    @Query("SELECT f FROM Feedback f WHERE f.customer = :customer AND f.isHidden = false")
    List<Feedback> findByCustomer(Customer customer);

    // Tìm tất cả Feedback chưa bị ẩn.
    @Query("SELECT f FROM Feedback f WHERE f.isHidden = false")
    List<Feedback> findAllNotHidden();

    // Tìm Feedback theo id và chưa bị ẩn.
    @Query("SELECT f FROM Feedback f WHERE f.Id = :id AND f.isHidden = false")
    Optional<Feedback> findByIdNotHidden(Long id);

    @Query("SELECT f FROM Feedback f WHERE f.customer = :customer AND f.isHidden = false")
    List<Feedback> findByCustomerNotHidden(Customer customer);

    @Query("SELECT f FROM Feedback f")
    List<Feedback> findAllIncludingHidden();

}
