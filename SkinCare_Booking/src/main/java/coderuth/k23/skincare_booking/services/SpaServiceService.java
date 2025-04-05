package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.SpaServiceRequestDTO;
import coderuth.k23.skincare_booking.dtos.response.SpaServiceResponseDTO;
import coderuth.k23.skincare_booking.models.CenterSchedule;
import coderuth.k23.skincare_booking.models.SpaService;
import coderuth.k23.skincare_booking.repositories.CenterScheduleRepository;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpaServiceService {

    @Autowired
    private SpaServiceRepository spaServiceRepository;

    @Autowired
    private CenterScheduleRepository centerScheduleRepository;

    // Quản lý dịch vụ
    public List<SpaService> getAllServices() {
        return spaServiceRepository.findAll();
    }

    public SpaService createService(SpaService spaService) {
        return spaServiceRepository.save(spaService);
    }

    public SpaService updateService(Long id, SpaService updatedService) {
        SpaService spaService = spaServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ!"));
        spaService.setName(updatedService.getName());
        spaService.setDescription(updatedService.getDescription());
        spaService.setPrice(updatedService.getPrice());
        spaService.setDuration(updatedService.getDuration());
        return spaServiceRepository.save(spaService);
    }

    public void deleteService(Long id,  RedirectAttributes redirectAttributes) {
        spaServiceRepository.deleteById(id);
    }

    // Quản lý lịch làm việc trung tâm
    public List<CenterSchedule> getAllSchedules() {
        return centerScheduleRepository.findAll();
    }

    public CenterSchedule createSchedule(CenterSchedule schedule) {
        return centerScheduleRepository.save(schedule);
    }

    public CenterSchedule updateSchedule(Long id, CenterSchedule updatedSchedule) {
        CenterSchedule schedule = centerScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc!"));
        schedule.setDayOfWeek(updatedSchedule.getDayOfWeek());
        schedule.setStartTime(updatedSchedule.getStartTime());
        schedule.setEndTime(updatedSchedule.getEndTime());
        schedule.setIsClosed(updatedSchedule.getIsClosed());
        return centerScheduleRepository.save(schedule);
    }
    
    public void deleteSchedule(Long id) {
        centerScheduleRepository.deleteById(id);
    }
}
