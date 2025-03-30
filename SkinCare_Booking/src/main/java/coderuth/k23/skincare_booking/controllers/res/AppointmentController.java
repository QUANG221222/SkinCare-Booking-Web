package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.services.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin("*") // Cho phép FE truy cập API
public class AppointmentController {

  @Autowired
  private AppointmentService appointmentService;

  @GetMapping
  public List<Appointment> getAllAppointments() {
    return appointmentService.getAllAppointments();
  }

  @GetMapping("/{id}")
  public Optional<Appointment> getAppointmentById(@PathVariable Long id) {
    return appointmentService.getAppointmentById(id);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createAppointment(@Valid @RequestBody Appointment appointment) {
    appointmentService.createAppointment(appointment);
    return ResponseEntity.ok(ApiResponse.success("You book successfully"));
  }

  @PutMapping("/{id}")
  public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
    return appointmentService.updateAppointment(id, appointment);
  }

  @DeleteMapping("/{id}")
  public void deleteAppointment(@PathVariable Long id) {
    appointmentService.deleteAppointment(id);
  }
}
