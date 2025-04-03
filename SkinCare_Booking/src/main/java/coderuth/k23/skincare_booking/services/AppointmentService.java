package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Lấy danh sách tất cả các lịch hẹn
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Lấy thông tin chi tiết của một lịch hẹn
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    // Tạo mới một lịch hẹn
    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // Cập nhật thông tin lịch hẹn
    public Appointment updateAppointment(Long id, Appointment newAppointment) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setCustomer(newAppointment.getCustomer());
            appointment.setSkinTherapist(newAppointment.getSkinTherapist());
            appointment.setSpaService(newAppointment.getSpaService());
            appointment.setStaff(newAppointment.getStaff());
            appointment.setManager(newAppointment.getManager());
            return appointmentRepository.save(appointment);
        }).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    // Xóa một lịch hẹn
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }
}
