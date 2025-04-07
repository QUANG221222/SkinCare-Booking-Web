package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.CustomerProfileRequest;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.User;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import coderuth.k23.skincare_booking.services.AppointmentService;
import coderuth.k23.skincare_booking.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

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

    @GetMapping("/contact")
    public String userContactPage() {
        return "user/contact";
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

            // Thêm thông báo thành công vào RedirectAttributes
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi vào RedirectAttributes
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

            // Thêm thông báo thành công vào RedirectAttributes
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi vào RedirectAttributes
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }

        return "redirect:/protected/customer/profile";
    }

    @GetMapping("/booking-history")
    public String bookingHistory(Model model) {
        // Fetch booking history from the database
        List<Appointment> bookings = appointmentService.getAppointmentsByCustomer(getCurrentCustomerId());
        model.addAttribute("bookings", bookings);
        return "user/customer/booking-history";
    }

    // Helper method to get current customer ID
    private UUID getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}

