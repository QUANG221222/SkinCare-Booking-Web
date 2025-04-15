package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.dtos.response.StaffInfoResponse;
import coderuth.k23.skincare_booking.services.AppointmentService;
import coderuth.k23.skincare_booking.services.PaymentService;
import coderuth.k23.skincare_booking.services.StaffService;
import coderuth.k23.skincare_booking.services.TherapistService;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.models.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/protected/staff")
@PreAuthorize("hasRole('MANAGER')")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private TherapistService therapistService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/home")
    public String adminPage() {
        return "admin/index"; // "user/index.html"
    }

    // Xem danh sách đặt dịch vụ đang chờ xử lý
    @GetMapping("/appointments/pending")
    public String listPendingAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.PENDING));
        return "admin/staff/pending_appointments";
    }

    // Check-in cho khách hàng
    @PostMapping("/appointments/check-in/{id}")
    public String checkIn(@PathVariable Long id) {
        appointmentService.checkIn(id);
        return "redirect:/protected/staff/appointments/checked-in";
    }

    // Xem danh sách đặt dịch vụ đã check-in
    @GetMapping("/appointments/checked-in")
    public String listCheckedInAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_IN));
        return "admin/staff/checked_in_appointments";
    }

    // Phân công chuyên viên
    @GetMapping("/appointments/assign/{id}")
    public String showAssignTherapistForm(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_IN)
                .stream().filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book service not found!"));
        model.addAttribute("appointment", appointment);
        model.addAttribute("therapists", therapistService.getAllTherapists());
        return "admin/staff/assign_therapist";
    }

    @PostMapping("/appointments/assign/{id}")
    public String assignTherapist(@PathVariable Long id, @RequestParam UUID therapistId) {
        appointmentService.assignTherapist(id, therapistId);
        return "redirect:/protected/staff/appointments/assigned";
    }

    // Xem danh sách đặt dịch vụ đã phân công
    @GetMapping("/appointments/assigned")
    public String listAssignedAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED));
        return "admin/staff/assigned_appointments";
    }

    // Xem danh sách đặt dịch vụ đã hoàn thành
    @GetMapping("/appointments/completed")
    public String listCompletedAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.COMPLETED));
        return "admin/staff/completed_appointments";
    }

    // Check-out cho khách hàng
    @PostMapping("/appointments/check-out/{id}")
    public String checkOut(@PathVariable Long id) {
        appointmentService.checkOut(id);
        return "redirect:/protected/staff/appointments/checked-out";
    }

    // Xem danh sách đặt dịch vụ đã check-out
    @GetMapping("/appointments/checked-out")
    public String listCheckedOutAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_OUT));
        return "admin/staff/checked_out_appointments";
    }
    // Hiển thị hóa đơn
    @GetMapping("/appointments/invoice/{id}")
    public String showInvoice(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_OUT)
                .stream().filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn!"));
        boolean isUnpaid = appointment.getPayments().stream().noneMatch(p -> p.getPaymentStatus() == Payment.PaymentStatus.PAID);
        model.addAttribute("appointment", appointment);
        model.addAttribute("isUnpaid", isUnpaid);
        return "admin/staff/invoice";
    }

    // Xác nhận thanh toán
    @PostMapping("/appointments/confirm-payment/{id}")
    public String confirmPayment(@PathVariable Long id, @RequestParam String paymentMethod) {
        appointmentService.confirmPayment(id, paymentMethod);
        return "redirect:/protected/staff/appointments/invoice/{id}";
    }

    // Hủy lịch hẹn
    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointmentByStaff(id);
        return "redirect:/protected/staff/appointments/pending";
    }
    // Xem danh sách hủy lịch hẹn
    @GetMapping("/appointments/cancelled")
    public String listCancelledAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CANCELLED));
        return "admin/staff/cancelled_appointments";
    }
    

    // Xem lịch sử thanh toán của tất cả khách hàng
    @GetMapping("/payments/history")
    public String viewPaymentHistory(Model model) {
        List<Payment> payments = paymentService.findAllPayments();
        model.addAttribute("payments", payments);
        return "admin/staff/payment_history";
    }
    // Endpoint để lấy thông tin cơ bản của tất cả nhân viên
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffInfoResponse>>> getAllStaff() {
        List<StaffInfoResponse> staff = staffService.getAllStaff();
        return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", staff));
    }
}

