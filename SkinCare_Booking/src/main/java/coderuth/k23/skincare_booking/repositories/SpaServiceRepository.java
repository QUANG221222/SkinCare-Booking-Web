package coderuth.k23.skincare_booking.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import coderuth.k23.skincare_booking.models.SpaService;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpaServiceRepository extends JpaRepository<SpaService, Long>{
    // Tìm các dịch vụ có tên chứa từ khóa (không phân biệt hoa thường)
    List<SpaService> findByNameLikeIgnoreCase(String name);
    // Tìm dịch vụ theo tên
    Optional<SpaService> findByName(String name);
    // Tìm các dịch vụ theo khoảng giá
    List<SpaService> findByPriceBetween(double min, double max);
    // Tìm các dịch vụ theo thời gian thực hiện
    List<SpaService> findByDurationBetween(int min, int max);
    // Tìm dịch vụ được đặt nhiều nhất (top 5)
    List<SpaService> findTop5ByOrderByAppointmentsDesc();
    // Kiểm tra sự tồn tại của dịch vụ theo tên
    boolean existsByName(String name);



}
