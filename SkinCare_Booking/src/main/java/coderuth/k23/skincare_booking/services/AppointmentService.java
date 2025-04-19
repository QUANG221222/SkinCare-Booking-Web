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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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

    // Customer books a service
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian đặt lịch không được ở quá khứ!");
        }
        LocalTime time = appointmentTime.toLocalTime();
        if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("Trung tâm chỉ hoạt động từ 8:00 đến 20:00!");
        }
        if (appointment.getSkinTherapist() != null) {
            validateTherapistAvailability(appointment.getSkinTherapist(), appointmentTime, appointment.getSpaService());
        }
        // Đảm bảo các trường bắt buộc không null
        if (appointment.getSpaService() == null || appointment.getSpaService().getId() == null) {
            throw new IllegalArgumentException("Dịch vụ không được để trống!");
        }
        if (appointment.getCustomer() == null || appointment.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Khách hàng không được để trống!");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Thời gian đặt lịch không được để trống!");
        }

        // Đặt trạng thái mặc định
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        // Khởi tạo danh sách payments nếu chưa có
        if (appointment.getPayments() == null) {
            appointment.setPayments(new ArrayList<>());
        }

        // Lưu Appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Kiểm tra xem đã có Payment nào chưa
        List<Payment> existingPayments = paymentRepository.findByAppointmentId(savedAppointment.getId());
        Payment payment;
        if (existingPayments.isEmpty()) {
            payment = new Payment();
            payment.setAppointment(savedAppointment);
            payment.setAmount(savedAppointment.getSpaService().getPrice());
            payment.setPaymentMethod("N/A");
            payment.setPaymentStatus(Payment.PaymentStatus.UNPAID);
            Payment savedPayment = paymentRepository.save(payment);
            savedAppointment.getPayments().add(savedPayment);
        }
        return appointmentRepository.save(savedAppointment);
    }

    // Check the therapist's schedule
    private void validateTherapistAvailability(SkinTherapist therapist, LocalDateTime appointmentTime, SpaService spaService) {
        // If no therapist is assigned or ID is invalid, skip the check
        if (therapist == null || therapist.getId() == null) {
            return;
        }
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
        // Kiểm tra xung đột lịch hẹn
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
                .orElseThrow(() -> new RuntimeException("Service booking not found!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.ASSIGNED) {
            throw new RuntimeException("Service booking is not in ASSIGNED status!");
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
            // Khởi tạo danh sách payments nếu chưa có
        if (appointment.getPayments() == null) {
            appointment.setPayments(new ArrayList<>());
        }
        // Kiểm tra xem đã có Payment nào chưa
        List<Payment> existingPayments = paymentRepository.findByAppointmentId(appointment.getId());
        Payment payment;
        if (existingPayments.isEmpty()) {
            payment = new Payment();
            payment.setAppointment(appointment);
            payment.setAmount(appointment.getSpaService().getPrice());
            payment.setPaymentMethod("N/A");
            payment.setPaymentStatus(Payment.PaymentStatus.UNPAID);
            Payment savedPayment = paymentRepository.save(payment);
            appointment.getPayments().add(savedPayment);
        }
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_OUT);
        return appointmentRepository.save(appointment);
    }

    // Xác nhận thanh toán cho lịch hẹn
    public Appointment confirmPayment(Long appointmentId, String paymentMethod) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn!"));
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Lịch hẹn chưa ở trạng thái CHECKED_OUT, không thể thanh toán!");
        }
        List<Payment> existingPayments = paymentRepository.findByAppointmentId(appointmentId);
        Payment payment;
        if (!existingPayments.isEmpty()) {
            payment = existingPayments.get(0);
            if (payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
                throw new RuntimeException("Lịch hẹn đã được thanh toán!");
            }
            payment.setAmount(appointment.getSpaService().getPrice()); // Gán lại amount
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        } else {
            payment = new Payment();
            payment.setAppointment(appointment);
            payment.setAmount(appointment.getSpaService().getPrice());
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        }
        Payment savedPayment = paymentRepository.save(payment);
        appointment.getPayments().add(savedPayment);
        return appointmentRepository.save(appointment);
    }

    
    // Hủy lịch hẹn (cho khách hàng)
    public Appointment cancelAppointmentByCustomer(Long appointmentId, UUID customerId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn!"));
        if (!appointment.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Bạn không có quyền hủy lịch hẹn này!");
        }
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy lịch hẹn ở trạng thái PENDING!");
        }
        LocalDateTime now = LocalDateTime.now();
        if (appointment.getAppointmentTime().isBefore(now.plusHours(24))) {
            throw new RuntimeException("Không thể hủy lịch hẹn trong vòng 24 giờ trước thời gian diễn ra!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        // Nếu có Payment nào đã PAID, cập nhật thành REFUNDED
        appointment.getPayments().stream()
        .filter(p -> p.getPaymentStatus() == Payment.PaymentStatus.PAID)
        .forEach(p -> {
            p.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(p);
        });

    return appointmentRepository.save(appointment);
    }

    // Hủy lịch hẹn (cho nhân viên/quản lý)
    public Appointment cancelAppointmentByStaff(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn!"));
        if (appointment.getStatus() == Appointment.AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Không thể hủy lịch hẹn đã CHECKED_OUT!");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        // Nếu có Payment nào đã PAID, cập nhật thành REFUNDED
        appointment.getPayments().stream()
        .filter(p -> p.getPaymentStatus() == Payment.PaymentStatus.PAID)
        .forEach(p -> {
            p.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(p);
        });

    return appointmentRepository.save(appointment);
    }

    // Lấy lịch sử thanh toán của customer
    public List<Payment> getPaymentHistoryByCustomer(UUID customerId) {
        return paymentRepository.findByAppointmentCustomerId(customerId);
    }

    // Lấy lịch sử thanh toán của một appointment
    public List<Payment> getPaymentsByAppointment(Long appointmentId) {
    return paymentRepository.findByAppointmentId(appointmentId);
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
}
