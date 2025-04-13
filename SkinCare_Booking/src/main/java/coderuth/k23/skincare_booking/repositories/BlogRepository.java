package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    // No additional method is needed as JpaRepository provides basic CRUD operations.
}
