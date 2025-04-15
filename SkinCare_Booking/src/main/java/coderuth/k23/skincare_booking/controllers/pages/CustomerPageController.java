package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.Payment;
import coderuth.k23.skincare_booking.models.SpaService;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import coderuth.k23.skincare_booking.services.AppointmentService;
import coderuth.k23.skincare_booking.services.CustomerService;
import coderuth.k23.skincare_booking.services.SpaServiceService;
import coderuth.k23.skincare_booking.services.TherapistService;

import coderuth.k23.skincare_booking.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/protected/customer")
public class CustomerPageController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    private SpaServiceService spaServiceService;

    @Autowired
    SpaServiceRepository spaServiceRepository;


    @Autowired
    private TherapistService therapistService;

     // Lấy ID khách hàng đã đăng nhập từ SecurityContext
     private UUID getLoggedInCustomerId() {
        // Thay thế bằng logic thực tế để lấy ID khách hàng đã đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not logged in!");
        }


        // Giả sử bạn có một lớp UserDetailsImpl chứa thông tin người dùng
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


        // Trả về ID khách hàng dưới dạng UUID
        return userDetails.getId();
    }

    // Hiển thị danh sách dịch vụ và form đặt dịch vụ
    @GetMapping("/appointment")
    public String showAppointmentForm(@RequestParam(value = "serviceId", required = false) Long serviceId, Model model) {
        Appointment appointment = new Appointment();
        if (serviceId != null) {
            // Lấy thông tin service đã chọn (bạn có thể thay bằng phương thức tìm theo id)
            SpaService service = spaServiceService.getServiceById(serviceId);
            appointment.setSpaService(service);
        }
        model.addAttribute("appointment", appointment);
        // Nạp thêm danh sách dịch vụ và chuyên viên nếu cần cho form lựa chọn thay đổi (hoặc chỉ nạp nếu chưa có service chọn trước)
        model.addAttribute("services", spaServiceService.getAllServices());
        model.addAttribute("therapists", therapistService.getAllTherapists());
        return "user/customer/appointment_form";  
    }
    // Khách hàng đặt dịch vụ
    @PostMapping("/appointment")
    @ResponseBody
    public ResponseEntity<?> createAppointment(@ModelAttribute Appointment appointment) {
        try {
            UUID customerId = getLoggedInCustomerId(); // Lấy ID khách hàng đã đăng nhập
            appointment.setCustomer(new Customer());
            appointment.getCustomer().setId(customerId);
            appointmentService.createAppointment(appointment);
            
            // Trả về JSON khi thành công
            return ResponseEntity.ok(Map.of("status", "success", "message", "Đặt dịch vụ thành công!"));
        } catch (RuntimeException ex) {
            // Nếu có lỗi, trả về JSON thông báo lỗi
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", ex.getMessage()));
        }
    }


    // Xem danh sách đặt dịch vụ của khách hàng
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        UUID customerId = getLoggedInCustomerId();
        model.addAttribute("appointments", appointmentService.getAppointmentsByCustomer(customerId));
        return "user/customer/appointments_list";
    }

     // Hủy lịch hẹn
     @PostMapping("/appointments/cancel/{id}")
     public String cancelAppointment(@PathVariable Long id) {
         UUID customerId = getLoggedInCustomerId();
         appointmentService.cancelAppointmentByCustomer(id, customerId);
         return "redirect:/protected/customer/appointments";
     }

    // Xem lịch sử thanh toán của customer
    @GetMapping("/payments/history")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentHistory() {
        UUID customerId = getLoggedInCustomerId(); // Giả sử có phương thức lấy ID của customer đang đăng nhập
        List<Payment> payments = appointmentService.getPaymentHistoryByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved successfully", payments));
    }

    @Autowired
    private FeedbackService feedbackService;

    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/home")
    public String userHomePage() {
        return "user/index"; // Load file src/main/resources/templates/user/index.html
    }

    @GetMapping("/about-us")
    public String userAboutUsPage() {
        return "user/about-us";
    }

    @GetMapping("/services")
    public String getAllServices(Model model) {
         // Lấy tất cả dịch vụ từ DB
        List<SpaService> services = spaServiceRepository.findAll();
        System.out.println("services.size() = " + services.size());

        model.addAttribute("services", services);
        return "user/services";
    }

    // Endpoint mới để hiển thị danh sách feedback
    @GetMapping("/feedbacks")
    public String userFeedbackListPage(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
        return "user/customer/Feedback-Manager";
    }

    // Chỉnh sửa Contact Page để tích hợp giao diện feedback
    @GetMapping("/contact")
    public String userContactPage(Model model, Principal principal) {
        String username = principal.getName();
        // Lấy danh sách Feedback của Customer và khởi tạo đối tượng FeedbackRequest cho form feedback
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
        model.addAttribute("feedbackRequest", new FeedbackRequest());
        return "user/customer/contact_Customer"; //File contact.html đã tích hợp giao diện feedback
    }

    // Xử lý gửi feedback từ trang contact
    @PostMapping("/contact")
    public String submitFeedback(
            @Valid @ModelAttribute("feedbackRequest") FeedbackRequest feedbackRequest,
            BindingResult result,
            Model model,
            Principal principal) {
        String username = principal.getName();

        if (result.hasErrors()) {
            model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
            return "user/customer/contact_Customer";
        }

        try {
            feedbackService.createFeedback(feedbackRequest);
            model.addAttribute("successMessage", "Feedback submitted successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error submitting feedback: " + e.getMessage());
        }

        model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
        model.addAttribute("feedbackRequest", new FeedbackRequest());
        return "user/customer/contact_Customer";
    }

    @GetMapping("/blog")
    public String userBlogPage() {
        return "user/blog";
    }

    @GetMapping("/skin-therapist")
    public String userSkinTherapistPage(Model model) {
        model.addAttribute("therapist", therapistService.getAllTherapists());
        return "user/skin-therapist";
    }
    
    @GetMapping("/profile")
    public String getCustomerProfile(Model model, Principal principal) {
        String username = principal.getName();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        model.addAttribute("customer", customer);
        return "user/customer/profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(
            @ModelAttribute EditProfileRequest profileRequest,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        try {
            // Cập nhật thông tin hồ sơ
            customerService.updateCustomerProfile(username, profileRequest);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }

        // Redirect về trang profile
        return "redirect:/protected/customer/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        try {
            // Kiểm tra và thay đổi mật khẩu
            customerService.changePassword(username, currentPassword, newPassword, confirmPassword);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            // Thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }

        return "redirect:/protected/customer/profile";
    }

    @GetMapping("/booking-history")
    public String bookingHistory(Model model) {
        // Lấy lịch sử đặt lịch từ cơ sở dữ liệu
        List<Appointment> bookings = appointmentService.getAppointmentsByCustomer(getLoggedInCustomerId());
        model.addAttribute("bookings", bookings);
        return "user/customer/booking-history";
    }
}
