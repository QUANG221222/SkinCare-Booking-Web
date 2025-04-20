package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.repositories.AppointmentRepository;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.repositories.PaymentRepository;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.web.client.RestTemplate;
import coderuth.k23.skincare_booking.dtos.response.VietQRResponse;

@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TherapistScheduleRepository therapistScheduleRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JavaMailSender mailSender;

   // Thông tin tài khoản ngân hàng để tạo mã QR
   private static final String BANK_ID = "970436";
   private static final String ACCOUNT_NO = "1030301953";
   private static final String ACCOUNT_NAME = "NGUYEN MINH THUAN";
   private static final String TEMPLATE = "compact2";
   private static final String VIETQR_API_URL = "https://api.vietqr.io/v2/generate";

   public void sendConfirmationEmail(Appointment appointment) {
       SimpleMailMessage message = new SimpleMailMessage();
       message.setTo(appointment.getCustomer().getEmail());
       message.setSubject("Xác nhận đặt lịch hẹn");
       message.setText("Hẹn của bạn vào " + appointment.getAppointmentTime() + " đã được tạo!");
       mailSender.send(message);
   }

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
            appointment.setStatus(Appointment.AppointmentStatus.ASSIGNED);
        } else {
            appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        }
        if (appointment.getSpaService() == null || appointment.getSpaService().getId() == null) {
            throw new IllegalArgumentException("Dịch vụ không được để trống!");
        }
        if (appointment.getCustomer() == null || appointment.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Khách hàng không được để trống!");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Thời gian đặt lịch không được để trống!");
        }

        Double servicePrice = appointment.getSpaService().getPrice();
        if (servicePrice == null || servicePrice <= 0) {
            throw new IllegalArgumentException("Dịch vụ '" + appointment.getSpaService().getName() + "' có giá không hợp lệ (ID: " + appointment.getSpaService().getId() + "). Vui lòng liên hệ quản trị viên.");
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        Payment payment = new Payment();
        payment.setAppointment(savedAppointment);
        payment.setAmount(servicePrice);
        payment.setPaymentMethod("PENDING"); // Nhân viên sẽ chọn phương thức sau
        payment.setPaymentStatus(Payment.PaymentStatus.UNPAID);
        Payment savedPayment = paymentRepository.save(payment);

        savedAppointment.setPayment(savedPayment);
        return appointmentRepository.save(savedAppointment);
    }

    private String generateQRCode(Double amount, String transactionId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = String.format(
                "{\"accountNo\":\"%s\",\"accountName\":\"%s\",\"acqId\":\"%s\",\"amount\":%.0f,\"addInfo\":\"%s\",\"format\":\"%s\"}",
                ACCOUNT_NO, ACCOUNT_NAME, BANK_ID, amount, transactionId, TEMPLATE
        );

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    VIETQR_API_URL, HttpMethod.POST, request, String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("VietQR API trả về lỗi: " + response.getStatusCode());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            VietQRResponse vietQRResponse = objectMapper.readValue(response.getBody(), VietQRResponse.class);

            if (!"00".equals(vietQRResponse.getCode())) {
                throw new RuntimeException("VietQR API trả về lỗi: " + vietQRResponse.getDesc());
            }

            if (vietQRResponse.getData() == null || vietQRResponse.getData().getQrDataURL() == null) {
                throw new RuntimeException("Không tìm thấy qrDataURL trong phản hồi từ VietQR");
            }

            return vietQRResponse.getData().getQrDataURL();
        } catch (Exception e) {
            logger.error("Failed to generate QR code: {}", e.getMessage());
            throw new RuntimeException("Không thể tạo mã QR thanh toán: " + e.getMessage());
        }
    }
   private void validateTherapistAvailability(SkinTherapist therapist, LocalDateTime appointmentTime, SpaService spaService) {
       if (therapist == null || therapist.getId() == null) {
           return;
       }
       List<TherapistSchedule> schedules = therapistScheduleRepository.findBySkinTherapistId(therapist.getId());
       boolean isAvailable = schedules.stream().anyMatch(schedule -> {
           boolean isDayMatching = schedule.getDayOfWeek().equalsIgnoreCase(appointmentTime.getDayOfWeek().toString());
           boolean isTimeMatching = appointmentTime.toLocalTime().isAfter(schedule.getStartTime()) &&
                                   appointmentTime.toLocalTime().isBefore(schedule.getEndTime());
           LocalTime serviceEndTime = appointmentTime.toLocalTime().plusMinutes(spaService.getDuration());
           boolean isDurationValid = serviceEndTime.isBefore(schedule.getEndTime()) || serviceEndTime.equals(schedule.getEndTime());
           return isDayMatching && isTimeMatching && isDurationValid;
       });

       if (!isAvailable) {
           throw new RuntimeException("Chuyên viên không có lịch trống vào thời điểm này hoặc thời gian dịch vụ vượt quá lịch làm việc!");
       }
       List<Appointment> existingAppointments = appointmentRepository.findBySkinTherapistId(therapist.getId());
       LocalDateTime serviceEndTime = appointmentTime.plusMinutes(spaService.getDuration());
       boolean hasConflict = existingAppointments.stream().anyMatch(a -> {
           LocalDateTime existingStart = a.getAppointmentTime();
           LocalDateTime existingEnd = existingStart.plusMinutes(a.getSpaService().getDuration());
           return a.getStatus() != Appointment.AppointmentStatus.CANCELLED &&
                  appointmentTime.isBefore(existingEnd) && serviceEndTime.isAfter(existingStart);
       });
       if (hasConflict) {
           throw new RuntimeException("Chuyên viên đã có lịch hẹn khác vào thời điểm này!");
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
    @Transactional
    public Appointment updatePaymentMethod(Long appointmentId, String paymentMethod) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found!"));

        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Appointment is not in CHECKED_OUT status, cannot update payment method!");
        }

        Payment payment = appointment.getPayment();
        if (payment == null) {
            throw new RuntimeException("Payment record not found for this appointment!");
        }
        if (payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
            throw new RuntimeException("Appointment has already been paid!");
        }

        payment.setPaymentMethod(paymentMethod);
        if ("QR".equals(paymentMethod) || "TRANSFER".equals(paymentMethod)) {
            String transactionId = UUID.randomUUID().toString();
            payment.setTransactionId(transactionId);
            String qrDataUrl = generateQRCode((payment.getAmount() * 23000), transactionId);
            try {
                String qrCodeFilePath = saveQRCodeAsFile(qrDataUrl); // Lưu file và lấy đường dẫn
                payment.setQrCodeDataUrl(qrCodeFilePath); // Lưu đường dẫn file
                logger.info("qrCodeFilePath: {} (length: {})", qrCodeFilePath, qrCodeFilePath.length());
            } catch (IOException e) {
                logger.error("Failed to save QR code as file: {}", e.getMessage());
                throw new RuntimeException("Không thể lưu mã QR dưới dạng file: " + e.getMessage());
            }
        } else {
            payment.setQrCodeDataUrl(null);
        }

        paymentRepository.save(payment);
        return appointmentRepository.save(appointment);
    }
  // Confirm payment for the appointment with payment method
//   @Transactional
//   public Appointment confirmPayment(Long appointmentId, String paymentMethod) {
//       Appointment appointment = appointmentRepository.findById(appointmentId)
//           .orElseThrow(() -> new RuntimeException("Appointment not found!"));
//       if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_OUT) {
//           throw new RuntimeException("Appointment is not in CHECKED_OUT status, cannot confirm payment!");
//       }

//       Payment payment = appointment.getPayment();
//       if (payment == null) {
//           throw new RuntimeException("Payment record not found for this appointment!");
//       }
//       if (payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
//           throw new RuntimeException("Appointment has already been paid!");
//       }
//       Double paymentAmount = payment.getAmount();
//       Double servicePrice = appointment.getSpaService().getPrice();
//       if (paymentAmount == null || servicePrice == null) {
//           throw new IllegalStateException("Payment amount or service price cannot be null!");
//       }
//       if (!paymentAmount.equals(servicePrice)) {
//           throw new IllegalStateException("Payment amount (" + paymentAmount + ") does not match service price (" + servicePrice + ")!");
//       }

//       // Cập nhật phương thức thanh toán và trạng thái
//       payment.setPaymentMethod(paymentMethod);
//       if ("QR".equals(paymentMethod) || "TRANSFER".equals(paymentMethod)) {
//           String transactionId = UUID.randomUUID().toString();
//           payment.setTransactionId(transactionId);
//           String qrDataUrl = generateQRCode(payment.getAmount(), transactionId);
//           payment.setQrCodeDataUrl(qrDataUrl);
//       }
//       payment.setPaymentStatus(Payment.PaymentStatus.PAID);
//       paymentRepository.save(payment);

//       return appointment;
//   }
    @Transactional
    public Appointment confirmPayment(Long appointmentId) { // Bỏ tham số paymentMethod
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

        // Không cần cập nhật paymentMethod hoặc tạo QR vì đã làm trước đó
        payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);

        return appointment;
    }
    private String saveQRCodeAsFile(String qrDataUrl) throws IOException {
        if (!qrDataUrl.startsWith("data:image/")) {
            return qrDataUrl;
        }
    
        String base64Data = qrDataUrl.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
    
        // Sử dụng đường dẫn tuyệt đối đến thư mục gốc của dự án
        String projectRoot = System.getProperty("user.dir"); // Lấy thư mục gốc của dự án
        String qrCodeDir = projectRoot + "/public/qr_codes";
        String fileName = "qrcode_" + UUID.randomUUID().toString() + ".png";
        String filePath = qrCodeDir + "/" + fileName;
    
        try {
            Files.createDirectories(Paths.get(qrCodeDir));
            Files.write(Paths.get(filePath), imageBytes);
        } catch (IOException e) {
            logger.error("Không thể lưu file mã QR: {}", e.getMessage());
            throw new IOException("Không thể lưu file mã QR", e);
        }
    
        return "/qr_codes/" + fileName;
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
    
    // Find appointments by username
    public List<Appointment> findAppointmentsByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found with username: " + username));
        return appointmentRepository.findByCustomerId(customer.getId());
    }
}
