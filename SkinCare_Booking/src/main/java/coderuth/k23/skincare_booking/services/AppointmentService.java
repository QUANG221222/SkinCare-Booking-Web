package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.repositories.AppointmentRepository;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    @Autowired
    private TherapistScheduleRepository therapistScheduleRepository;

    // Khách hàng đặt dịch vụ
    public Appointment createAppointment(Appointment appointment) {
        // Kiểm tra lịch làm việc của chuyên viên (nếu có)
        if (appointment.getSkinTherapist() != null) {
            validateTherapistAvailability(appointment.getSkinTherapist(), appointment.getAppointmentTime());
        }
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        return appointmentRepository.save(appointment);
    }

    // Kiểm tra lịch làm việc của chuyên viên
    private void validateTherapistAvailability(SkinTherapist therapist, LocalDateTime bookingTime) {
        List<TherapistSchedule> schedules = therapistScheduleRepository.findBySkinTherapistId(therapist.getId());
        boolean isAvailable = schedules.stream().anyMatch(schedule ->
                schedule.getDayOfWeek().equalsIgnoreCase(bookingTime.getDayOfWeek().toString()) &&
                bookingTime.toLocalTime().isAfter(schedule.getStartTime()) &&
                bookingTime.toLocalTime().isBefore(schedule.getEndTime())
        );
        if (!isAvailable) {
            throw new RuntimeException("Chuyên viên không có lịch làm việc vào thời điểm này!");
        }
    }

    // Nhân viên check-in cho khách hàng
    public Appointment checkIn(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt dịch vụ!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("Đặt dịch vụ không ở trạng thái PENDING!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_IN);
        return appointmentRepository.save(appointment);
    }

    // Nhân viên phân công chuyên viên (nếu khách hàng không chỉ định)
    public Appointment assignTherapist(Long appointmentId, UUID therapistId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt dịch vụ!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_IN) {
            throw new RuntimeException("Đặt dịch vụ không ở trạng thái CHECKED_IN!");
        }
        if (appointment.getSkinTherapist() != null) {
            throw new RuntimeException("Đặt dịch vụ đã có chuyên viên được chỉ định!");
        }
        SkinTherapist therapist = skinTherapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên!"));
        validateTherapistAvailability(therapist, appointment.getAppointmentTime());
        appointment.setSkinTherapist(therapist);
        appointment.setStatus(Appointment.AppointmentStatus.ASSIGNED);
        return appointmentRepository.save(appointment);
    }

    // Chuyên viên ghi nhận kết quả thực hiện
    public Appointment recordResult(Long appointmentId, String result) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt dịch vụ!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.ASSIGNED) {
            throw new RuntimeException("Đặt dịch vụ không ở trạng thái ASSIGNED!");
        }
        appointment.setResult(result);
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    // Nhân viên check-out cho khách hàng
    public Appointment checkOut(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt dịch vụ!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Đặt dịch vụ không ở trạng thái COMPLETED!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_OUT);
        return appointmentRepository.save(appointment);
    }

    // Lấy danh sách đặt dịch vụ theo trạng thái
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    // Lấy danh sách đặt dịch vụ của khách hàng
    public List<Appointment> getAppointmentsByCustomer(UUID customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    // Lấy danh sách đặt dịch vụ của chuyên viên
    public List<Appointment> getAppointmentsByTherapist(UUID therapistId) {
        return appointmentRepository.findBySkinTherapistId(therapistId);
    }
}
