package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.repositories.AppointmentRepository;
import coderuth.k23.skincare_booking.repositories.PaymentRepository;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    @Autowired
    private TherapistScheduleRepository therapistScheduleRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(Appointment appointment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(appointment.getCustomer().getEmail());
        message.setSubject("Appointment Confirmation");
        message.setText("Your appointment at " + appointment.getAppointmentTime() + " has been created!");
        mailSender.send(message);
    }

    // Customer books a service
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time cannot be in the past!");
        }
        LocalTime time = appointmentTime.toLocalTime();
        if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("The center operates only from 8:00 AM to 8:00 PM!");
        }
        // Validate and verify SkinTherapist if specified
        if (appointment.getSkinTherapist() != null) {
            SkinTherapist therapist = skinTherapistRepository.findById(appointment.getSkinTherapist().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Therapist does not exist with ID: " + appointment.getSkinTherapist().getId()));
            validateTherapistAvailability(therapist, appointmentTime, appointment.getSpaService());
            appointment.setSkinTherapist(therapist);
            appointment.setStatus(Appointment.AppointmentStatus.ASSIGNED);
        } else {
            appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        }
           
        if (appointment.getSpaService() == null || appointment.getSpaService().getId() == null) {
            throw new IllegalArgumentException("Service cannot be empty!");
        }
        if (appointment.getCustomer() == null || appointment.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer cannot be empty!");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time cannot be empty!");
        }   

        // Validate service price
        Double servicePrice = appointment.getSpaService().getPrice();
        if (servicePrice == null || servicePrice <= 0) {
            throw new IllegalArgumentException("Service price must be greater than 0 for spa_service_id: " + appointment.getSpaService().getId());
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        Payment payment = new Payment();
        payment.setAppointment(savedAppointment);
        payment.setAmount(servicePrice);
        payment.setPaymentMethod("N/A");
        payment.setPaymentStatus(Payment.PaymentStatus.UNPAID);
        Payment savedPayment = paymentRepository.save(payment);

        savedAppointment.setPayment(savedPayment);
        return appointmentRepository.save(savedAppointment);
    }

    // Check the therapist's schedule
    private void validateTherapistAvailability(SkinTherapist therapist, LocalDateTime appointmentTime, SpaService spaService) {
        // Get the therapist's schedule
        List<TherapistSchedule> schedules = therapistScheduleRepository.findBySkinTherapistId(therapist.getId());
        boolean isAvailable = schedules.stream().anyMatch(schedule -> {
            // Check if the appointment day matches the therapist's schedule
            boolean isDayMatching = schedule.getDayOfWeek().equalsIgnoreCase(appointmentTime.getDayOfWeek().toString());

            // Check if the appointment time falls within the therapist's available hours
            boolean isTimeMatching = appointmentTime.toLocalTime().isAfter(schedule.getStartTime()) &&
                                    appointmentTime.toLocalTime().isBefore(schedule.getEndTime());

            // Calculate the end time of the service based on its duration
            LocalTime serviceEndTime = appointmentTime.toLocalTime().plusMinutes(spaService.getDuration());

            // Check if the service duration fits within the available time
            boolean isDurationValid = serviceEndTime.isBefore(schedule.getEndTime()) || serviceEndTime.equals(schedule.getEndTime());

            return isDayMatching && isTimeMatching && isDurationValid;
        });

        // If no suitable schedule is found, throw an exception
        if (!isAvailable) {
            throw new RuntimeException("The therapist is not available at this time or the service duration exceeds the available time!");
        }
        // Check for appointment conflicts
        List<Appointment> existingAppointments = appointmentRepository.findBySkinTherapistId(therapist.getId());
        LocalDateTime serviceEndTime = appointmentTime.plusMinutes(spaService.getDuration());
        boolean hasConflict = existingAppointments.stream().anyMatch(a -> {
            LocalDateTime existingStart = a.getAppointmentTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(a.getSpaService().getDuration());
            return a.getStatus() != Appointment.AppointmentStatus.CANCELLED &&
                appointmentTime.isBefore(existingEnd) && serviceEndTime.isAfter(existingStart);
        });
        if (hasConflict) {
            throw new RuntimeException("Therapist already has an appointment at this time!");
        }
    }

    // Staff checks in the customer
    public Appointment checkIn(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("Appointment is not in PENDING status!");
        }
        if (appointment.getSkinTherapist() != null) {
            appointment.setStatus(Appointment.AppointmentStatus.ASSIGNED);
        } else {
            appointment.setStatus(Appointment.AppointmentStatus.CHECKED_IN);
        }
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
        validateTherapistAvailability(therapist, appointment.getAppointmentTime(), appointment.getSpaService());
        appointment.setSkinTherapist(therapist);
        appointment.setStatus(Appointment.AppointmentStatus.ASSIGNED);
        return appointmentRepository.save(appointment);
    }

    // Therapist records the result of the service
    public Appointment recordResult(Long appointmentId, String result) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));
        if (!Appointment.AppointmentStatus.ASSIGNED.equals(appointment.getStatus())) {
            throw new RuntimeException("Appointment must be in ASSIGNED status to record the result! Current status: " + appointment.getStatus());
        }
        appointment.setResult(result);
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    // Staff checks out the customer
    @Transactional
    public Appointment checkOut(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Service booking not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Service booking is not in COMPLETED status!");
        }
        if (appointment.getPayment() == null) {
            throw new RuntimeException("Payment record not found for this appointment!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_OUT);
        return appointmentRepository.save(appointment);
    }


    // Confirm payment for the appointment
    @Transactional
    public Appointment confirmPayment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Appointment is not in CHECKED_OUT status, cannot confirm payment!");
        }

        Payment payment = appointment.getPayment();
        if (payment == null) {
            throw new RuntimeException("Payment record not found for this appointment!");
        }
        if (payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
            throw new RuntimeException("Appointment has already been paid!");
        }
        Double paymentAmount = payment.getAmount();
        Double servicePrice = appointment.getSpaService().getPrice();
        if (paymentAmount == null || servicePrice == null) {
            throw new IllegalStateException("Payment amount or service price cannot be null!");
        }
        if (!paymentAmount.equals(servicePrice)) {
            throw new IllegalStateException("Payment amount (" + paymentAmount + ") does not match service price (" + servicePrice + ")!");
        }
        

        payment.setPaymentMethod("N/A");
        payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);

        return appointment;
    }

    // Cancel appointment (for customers)
    @Transactional
    public Appointment cancelAppointmentByCustomer(Long appointmentId, UUID customerId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        if (!appointment.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("You do not have permission to cancel this appointment!");
        }
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("Only appointments in PENDING status can be canceled!");
        }
        LocalDateTime now = LocalDateTime.now();
        if (appointment.getAppointmentTime().isBefore(now.plusHours(24))) {
            throw new RuntimeException("Cannot cancel appointments within 24 hours of the scheduled time!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        Payment payment = appointment.getPayment();
        if (payment != null && payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
            payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }

        return appointmentRepository.save(appointment);
    }

    // Cancel appointment (for staff/management)
    @Transactional
    public Appointment cancelAppointmentByStaff(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        if (appointment.getStatus() == Appointment.AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Cannot cancel an appointment that has been CHECKED_OUT!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        Payment payment = appointment.getPayment();
        if (payment != null && payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
            payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }

        return appointmentRepository.save(appointment);
    }
    // Retrieve all payment records
    public Page<Payment> getPaymentHistoryByCustomer(UUID customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return paymentRepository.findByAppointmentCustomerId(customerId, pageable);
    }

    public Page<Payment> getPaymentHistoryByCustomerIdAndFilters(
            UUID customerId,
            int page,
            int size,
            String status,
            String startDate,
            String endDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format!");
        }

        // Normalize status
        String normalizedStatus = status != null && !status.isEmpty() ? status.toUpperCase() : null;
        if (normalizedStatus != null && !isValidPaymentStatus(normalizedStatus)) {
            throw new IllegalArgumentException("Invalid payment status! Accepted: UNPAID, PAID, REFUNDED");
        }

        return paymentRepository.findByAppointmentCustomerIdAndFilters(
                customerId, normalizedStatus, start, end, pageable);
    }

    private boolean isValidPaymentStatus(String status) {
        try {
            Payment.PaymentStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Retrieve payment history for an appointment
    public Payment getPaymentByAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        return appointment.getPayment();
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
    // Retrieve an appointment by ID
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    // Retrieve all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Update an appointment
    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    // Determine the payment status of an appointment
    public String getPaymentStatusDescription(Appointment appointment) {
        if (appointment.getPayment() == null) {
            return "Unpaid";
        }
        Payment payment = appointment.getPayment();
        if (payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
            return "Paid";
        } else if (payment.getPaymentStatus() == Payment.PaymentStatus.REFUNDED) {
            return "Refunded";
        } else {
            return "Unpaid";
        }
    }
    // Method to calculate revenue for a specific time range
    public double calculateCurrentMonthRevenue() {
        LocalDate today = LocalDate.now(); // April 18, 2025
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // April 1, 2025
        LocalDateTime start = startOfMonth.atStartOfDay(); // Start of April 1
        LocalDateTime end = LocalDateTime.now(); // Current moment (April 18, 2025, 11:13 PM PDT)

        List<Payment> paidPayments = paymentRepository.findByCreatedAtBetweenAndPaymentStatus(start, end, Payment.PaymentStatus.PAID);
        double revenue = paidPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        logger.info("Calculated current month revenue (only PAID payments): ${}", revenue);
        return revenue;
    }

    public double calculateCurrentYearRevenue() {
        LocalDate today = LocalDate.now(); // Ví dụ: April 18, 2025
        LocalDate startOfYear = today.with(TemporalAdjusters.firstDayOfYear()); // January 1, 2025
        LocalDateTime start = startOfYear.atStartOfDay(); // Bắt đầu từ 00:00 ngày 1/1/2025
        LocalDateTime end = LocalDateTime.now(); // Thời điểm hiện tại (April 18, 2025, 11:13 PM)

        List<Payment> paidPayments = paymentRepository.findByCreatedAtBetweenAndPaymentStatus(
                start,
                end,
                Payment.PaymentStatus.PAID
        );

        double revenue = paidPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        logger.info("Calculated current year revenue (only PAID payments): ${}", revenue);
        return revenue;
    }
}
