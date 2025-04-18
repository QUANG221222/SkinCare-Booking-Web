package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.models.Manager;
import coderuth.k23.skincare_booking.models.Staff;
import coderuth.k23.skincare_booking.repositories.StaffRepository;
import coderuth.k23.skincare_booking.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/protected/staff")
@PreAuthorize("hasRole('STAFF')")
public class StaffPageController {

    @Autowired
    StaffService staffService;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    TherapistService therapistService;

    @Autowired
    CustomerService customerService;

    @Autowired
    SpaServiceService spaServiceService;

    @Autowired
    private CenterScheduleService centerScheduleService;

    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/home")
    public String adminPage(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        model.addAttribute("staff", staff);
        return "admin/index"; // "user/index.html"
    }

    @GetMapping("/profile")
    public String getManagerProfile(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        model.addAttribute("staff", staff);
        return "admin/Profile/profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(
            @ModelAttribute EditProfileRequest profileRequest,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        try {
            // Cập nhật thông tin hồ sơ
            staffService.updateStaffProfile(username, profileRequest);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }

        // Redirect về trang profile
        return "redirect:/protected/staff/profile";
    }

    @GetMapping("/security")
    public String getSecurity(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        model.addAttribute("staff", staff);
        return "admin/Security/security";
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
            staffService.changePassword(username, currentPassword, newPassword, confirmPassword);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            // Thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }

        return "redirect:/protected/staff/security";
    }

    @GetMapping("/list-therapist")
    public String getTherapistList(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        model.addAttribute("staff", staff);
        model.addAttribute("therapistList", therapistService.getAllTherapists());
        return "admin/Users/ListTherapist/listTherapist";
    }

    @GetMapping("/list-customer")
    public String getCustomerList(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        model.addAttribute("staff", staff);
        model.addAttribute("customerList", customerService.getAllCustomers());
        return "admin/Users/ListCustomer/listCustomer";
    }

    @GetMapping("/spa-services")
    public String spaServicesPage(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        model.addAttribute("staff", staff);
        model.addAttribute("spaServicesList", spaServiceService.getAllServices());
        return "/admin/SpaServices/ListSpaServices/listSpaServices";
    }

    @GetMapping("/center-schedule")
    public String getCenterSchedule(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        model.addAttribute("staff", staff);
        model.addAttribute("scheduleList", centerScheduleService.getAllSchedules());
        return "admin/Schedules/centerSchedule";
    }
}
