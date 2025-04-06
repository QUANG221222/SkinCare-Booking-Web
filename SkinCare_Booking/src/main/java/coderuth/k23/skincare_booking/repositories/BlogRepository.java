package coderuth.k23.skincare_booking.repositories;

import coderuth.k23.skincare_booking.models.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}