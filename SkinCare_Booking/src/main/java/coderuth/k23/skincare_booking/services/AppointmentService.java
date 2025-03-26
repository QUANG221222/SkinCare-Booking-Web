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

  public List<Appointment> getAllAppointments() {
    return appointmentRepository.findAll();
  }

  public Optional<Appointment> getAppointmentById(Long id) {
    return appointmentRepository.findById(id);
  }

  public Appointment createAppointment(Appointment appointment) {

    return appointmentRepository.save(appointment);
  }

  public Appointment updateAppointment(Long id, Appointment newAppointment) {
    return appointmentRepository.findById(id).map(appointment -> {
      appointment.setCustomer(newAppointment.getCustomer());
      appointment.setSkinTherapist(newAppointment.getSkinTherapist());
      appointment.setSpaService(newAppointment.getSpaService());
      appointment.setAppointmentTime(newAppointment.getAppointmentTime());
      appointment.setStatus(newAppointment.getStatus());
      return appointmentRepository.save(appointment);
    }).orElseThrow(() -> new RuntimeException("Appointment not found"));
  }

  public void deleteAppointment(Long id) {
    appointmentRepository.deleteById(id);
  }
}
