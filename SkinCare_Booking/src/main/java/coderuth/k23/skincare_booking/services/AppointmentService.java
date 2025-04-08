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

    // Customer books a service
    public Appointment createAppointment(Appointment appointment) {
        // Check if SkinTherapist is invalid
        if (appointment.getSkinTherapist() != null && appointment.getSkinTherapist().getId() == null) {
            appointment.setSkinTherapist(null); // Reset to null if invalid
        }
    
        // Only check schedule if a therapist is assigned
        if (appointment.getSkinTherapist() != null) {
            validateTherapistAvailability(appointment.getSkinTherapist(), appointment.getAppointmentTime());
        }
    
        // Log the value of skinTherapist
        System.out.println("SkinTherapist: " + appointment.getSkinTherapist());
    
        // Set default status for the appointment
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
    
        // Save the appointment to the database
        return appointmentRepository.save(appointment);
    }

    // Check the therapist's schedule
    private void validateTherapistAvailability(SkinTherapist therapist, LocalDateTime appointmentTime) {
        // If no therapist is assigned or ID is invalid, skip the check
        if (therapist == null || therapist.getId() == null) {
            return;
        }
    
        // Get the therapist's schedule
        List<TherapistSchedule> schedules = therapistScheduleRepository.findBySkinTherapistId(therapist.getId());
        boolean isAvailable = schedules.stream().anyMatch(schedule ->
                schedule.getDayOfWeek().equalsIgnoreCase(appointmentTime.getDayOfWeek().toString()) &&
                appointmentTime.toLocalTime().isAfter(schedule.getStartTime()) &&
                appointmentTime.toLocalTime().isBefore(schedule.getEndTime())
        );
    
        // If no suitable schedule is found, throw an exception
        if (!isAvailable) {
            throw new RuntimeException("The therapist is not available at this time!");
        }
    }

    // Staff checks in the customer
    public Appointment checkIn(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("Appointment is not in PENDING status!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_IN);
        return appointmentRepository.save(appointment);
    }

    // Staff assigns a therapist (if the customer did not specify one)
    public Appointment assignTherapist(Long appointmentId, UUID therapistId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_IN) {
            throw new RuntimeException("Appointment is not in CHECKED_IN status!");
        }
        if (appointment.getSkinTherapist() != null) {
            throw new RuntimeException("Appointment already has an assigned therapist!");
        }
        SkinTherapist therapist = skinTherapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found!"));
        validateTherapistAvailability(therapist, appointment.getAppointmentTime());
        appointment.setSkinTherapist(therapist);
        appointment.setStatus(Appointment.AppointmentStatus.ASSIGNED);
        return appointmentRepository.save(appointment);
    }

    // Therapist records the result of the service
    public Appointment recordResult(Long appointmentId, String result) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Service booking not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.ASSIGNED) {
            throw new RuntimeException("Service booking is not in ASSIGNED status!");
        }
        appointment.setResult(result);
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    // Staff checks out the customer
    public Appointment checkOut(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Service booking not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Service booking is not in COMPLETED status!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_OUT);
        return appointmentRepository.save(appointment);
    }

    // Get a list of appointments by status
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    // Get a list of appointments for a customer
    public List<Appointment> getAppointmentsByCustomer(UUID customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    // Get a list of appointments for a therapist
    public List<Appointment> getAppointmentsByTherapist(UUID therapistId) {
        return appointmentRepository.findBySkinTherapistId(therapistId);
    }
}
