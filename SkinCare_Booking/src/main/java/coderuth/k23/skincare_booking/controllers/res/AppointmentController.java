package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.request.AppointmentRequest;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.SpaService;
import coderuth.k23.skincare_booking.repositories.AppointmentRepository;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin("*")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    @Autowired
    private SpaServiceRepository spaServiceRepository;

    // Lấy danh sách tất cả các lịch hẹn
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return ResponseEntity.ok(appointments);
    }

    // Lấy thông tin chi tiết của một lịch hẹn
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Tạo mới một lịch hẹn
    @PostMapping
public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
    // Tìm Customer từ customerId
    Customer customer = customerRepository.findById(appointmentRequest.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Tìm SkinTherapist từ skinTherapistId
    SkinTherapist skinTherapist = skinTherapistRepository.findById(appointmentRequest.getSkinTherapistId())
            .orElseThrow(() -> new IllegalArgumentException("Skin Therapist not found"));

    // Tìm SpaService từ spaServiceId
    SpaService spaService = spaServiceRepository.findById(appointmentRequest.getSpaServiceId())
            .orElseThrow(() -> new IllegalArgumentException("Spa Service not found"));

    // Tạo mới Appointment
    Appointment appointment = new Appointment();
    appointment.setCustomer(customer);
    appointment.setSkinTherapist(skinTherapist);
    appointment.setSpaService(spaService);
    appointment.setAppointmentTime(appointmentRequest.getAppointmentTime());
    appointment.setStatus(appointmentRequest.getStatus());

    Appointment savedAppointment = appointmentRepository.save(appointment);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
}

    // Cập nhật thông tin lịch hẹn
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment updatedAppointment) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setAppointmentTime(updatedAppointment.getAppointmentTime());
            appointment.setStatus(updatedAppointment.getStatus());

            // Cập nhật các liên kết nếu cần
            if (updatedAppointment.getCustomer() != null) {
                Customer customer = customerRepository.findById(updatedAppointment.getCustomer().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
                appointment.setCustomer(customer);
            }

            if (updatedAppointment.getSkinTherapist() != null) {
                SkinTherapist skinTherapist = skinTherapistRepository.findById(updatedAppointment.getSkinTherapist().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skin Therapist not found"));
                appointment.setSkinTherapist(skinTherapist);
            }

            if (updatedAppointment.getSpaService() != null) {
                SpaService spaService = spaServiceRepository.findById(updatedAppointment.getSpaService().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Spa Service not found"));
                appointment.setSpaService(spaService);
            }

            Appointment savedAppointment = appointmentRepository.save(appointment);
            return ResponseEntity.ok(savedAppointment);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Xóa một lịch hẹn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        if (!appointmentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        appointmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}