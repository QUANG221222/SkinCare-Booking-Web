package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import coderuth.k23.skincare_booking.services.AppointmentService;
import coderuth.k23.skincare_booking.services.CustomerService;
import coderuth.k23.skincare_booking.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private FeedbackService feedbackService;

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
        // In chi tiết từng service xem có gì
        services.forEach(s -> System.out.println(
            "ID: " + s.getId()
            + ", Name: " + s.getName()
            + ", ImageURL: " + s.getImageUrl()
        ));

        model.addAttribute("services", services);
        return "user/services";
    }

    // Hiển thị danh sách dịch vụ và form đặt dịch vụ
    @GetMapping("/appointment")
    public String showAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("services", spaServiceService.getAllServices());
        model.addAttribute("therapists", therapistService.getAllTherapists());
        return "user/customer/appointment_form";
    }


    // Khách hàng đặt dịch vụ
    @PostMapping("/appointment")
    public String createAppointment(@ModelAttribute Appointment appointment, Model model) {
        try {
            UUID customerId = getLoggedInCustomerId(); // Lấy ID khách hàng đã đăng nhập
            appointment.setCustomer(new Customer());
            appointment.getCustomer().setId(customerId);
            appointmentService.createAppointment(appointment);
            return "redirect:/protected/customer/appointments";
        } catch (RuntimeException ex) {
            // Thêm thông báo lỗi vào model
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("services", spaServiceService.getAllServices());
            model.addAttribute("therapists", therapistService.getAllTherapists());
            return "user/customer/appointment_form"; // Quay lại form đặt lịch
        }
    }


    // Xem danh sách đặt dịch vụ của khách hàng
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        UUID customerId = getLoggedInCustomerId();
        model.addAttribute("appointments", appointmentService.getAppointmentsByCustomer(customerId));
        return "user/customer/appointments_list";
    }

    public String userServicesPage() {
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
