package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.CustomerProfileRequest;
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
    public String userServicesPage() {
        return "user/services";
    }

    // Chỉnh sửa Contact Page để tích hợp giao diện feedback
    @GetMapping("/contact")
    public String userContactPage(Model model) {
        //khởi tạo đối tượng FeedbackRequest cho form feedback
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

    // Endpoint mới để hiển thị danh sách feedback
    @GetMapping("/feedbacks")
    public String userFeedbackListPage(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
        return "user/customer/Feedback-Manager";
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
            @ModelAttribute CustomerProfileRequest profileRequest,
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
        List<Appointment> bookings = appointmentService.getAppointmentsByCustomer(getCurrentCustomerId());
        model.addAttribute("bookings", bookings);
        return "user/customer/booking-history";
    }

    // Helper method để lấy ID của Customer hiện hành
    private UUID getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
