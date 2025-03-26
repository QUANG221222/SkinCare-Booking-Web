package coderuth.k23.skincare_booking.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import coderuth.k23.skincare_booking.models.SpaService;

@Repository
public interface SpaServiceRepository extends JpaRepository<SpaService, Long> {
    // Tìm dịch vụ theo tên
    Optional<SpaService> findByName(String name);

    // Tìm danh sách dịch vụ theo khoảng giá
    List<SpaService> findByPriceBetween(double minPrice, double maxPrice);

    // Tìm danh sách dịch vụ theo khoảng thời lượng
    List<SpaService> findByDurationBetween(int minDuration, int maxDuration);

    // Kiểm tra sự tồn tại của dịch vụ theo tên
    boolean existsByName(String name);

    // Tìm kiếm dịch vụ theo tên (không phân biệt chữ hoa/thường)
    List<SpaService> findByNameContainingIgnoreCase(String name);
}