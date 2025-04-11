package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.CustomerProfileRequest;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import coderuth.k23.skincare_booking.services.AppointmentService;
import coderuth.k23.skincare_booking.services.CustomerService;
import coderuth.k23.skincare_booking.services.SpaServiceService;
import coderuth.k23.skincare_booking.services.TherapistService;

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

     @Autowired
    private SpaServiceService spaServiceService;


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
    public String showAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("services", spaServiceService.getAllServices());
        model.addAttribute("therapists", therapistService.getAllTherapists());
        return "user/customer/appointment_form";
    }


    // Khách hàng đặt dịch vụ
    @PostMapping("/appointment")
    public String createAppointment(@ModelAttribute Appointment appointment) {
        UUID customerId = getLoggedInCustomerId(); // Lấy ID khách hàng đã đăn
        appointment.setCustomer(new Customer());
        appointment.getCustomer().setId(customerId);
        appointmentService.createAppointment(appointment);
        return "redirect:/protected/customer/appointments";
    }


    // Xem danh sách đặt dịch vụ của khách hàng
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        UUID customerId = getLoggedInCustomerId();
        model.addAttribute("appointments", appointmentService.getAppointmentsByCustomer(customerId));
        return "user/customer/appointments_list";
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

