package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.CenterSchedule;
import coderuth.k23.skincare_booking.repositories.CenterScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CenterScheduleService {

    @Autowired
    private CenterScheduleRepository centerScheduleRepository;

    // Lấy danh sách tất cả lịch làm việc
    public List<CenterSchedule> getAllSchedules() {
        return centerScheduleRepository.findAll();
    }

    // Tạo mới lịch làm việc
    public CenterSchedule createSchedule(CenterSchedule schedule) {
        return centerScheduleRepository.save(schedule);
    }

    // Cập nhật lịch làm việc
    public CenterSchedule updateSchedule(Long id, CenterSchedule updatedSchedule) {
        CenterSchedule schedule = centerScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found!"));
        schedule.setDayOfWeek(updatedSchedule.getDayOfWeek());
        schedule.setStartTime(updatedSchedule.getStartTime());
        schedule.setEndTime(updatedSchedule.getEndTime());
        schedule.setIsClosed(updatedSchedule.getIsClosed());
        return centerScheduleRepository.save(schedule);
    }

    // Xóa lịch làm việc
    public void deleteSchedule(Long id) {
        centerScheduleRepository.deleteById(id);
    }
}