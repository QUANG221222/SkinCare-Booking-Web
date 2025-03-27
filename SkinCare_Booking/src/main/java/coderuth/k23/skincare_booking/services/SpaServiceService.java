package coderuth.k23.skincare_booking.services;
import coderuth.k23.skincare_booking.models.SpaService;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SpaServiceService {
    @Autowired
    private SpaServiceRepository spaServiceRepository;

    // Lấy danh sách all dịch vụ
    public List<SpaService> getAllServices() {
        return spaServiceRepository.findAll();
    }

    // Tìm dịch vụ theo tên
    public Optional<SpaService> getServiceByName(String name) {
        return spaServiceRepository.findByName(name);
    }

    // Tìm theo id
    public Optional<SpaService> getServiceById(Long id) {
        return spaServiceRepository.findById(id);
    }

    // Tìm theo từ khóa
    public List<SpaService> searchByName(String keyword) {
        return spaServiceRepository.findByNameLikeIgnoreCase("%" + keyword + "%");
    }

    // Tìm theo khoảng giá
    public List<SpaService> searchByPriceRange(double min, double max) {
        return spaServiceRepository.findByPriceBetween(min, max);
    }

    // Tìm theo khoảng time thực hiện
    public List<SpaService> searchByDurationRange(int min, int max) {
        return spaServiceRepository.findByDurationBetween(min, max);
    }

    // Tìm top 5 dịch vụ được đặt nhiều nhất
    public List<SpaService> getTop5MostBookedServices() {
        return spaServiceRepository.findTop5ByOrderByAppointmentsDesc();
    }

    // Thêm dịch vụ mới
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public SpaService addService(SpaService service) {
        if (spaServiceRepository.existsByName(service.getName())) {
            throw new RuntimeException("Service already exists!");
        }
        return spaServiceRepository.save(service);
    }

    // Cập nhật dịch vụ
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public SpaService updateService(Long id, SpaService newService) {
        return spaServiceRepository.findById(id)
                .map(existingService -> {
                    existingService.setName(newService.getName());
                    existingService.setPrice(newService.getPrice());
                    existingService.setDuration(newService.getDuration());
                    return spaServiceRepository.save(existingService);
                }).orElseThrow(() -> new RuntimeException("Service does not exist!"));
    }

    // Xóa dịch vụ
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deleteService(Long id) {
        if (!spaServiceRepository.existsById(id)) {
            throw new RuntimeException("Service does not exist!");
        }
        spaServiceRepository.deleteById(id);
    }






}
