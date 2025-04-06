package coderuth.k23.skincare_booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import coderuth.k23.skincare_booking.models.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}