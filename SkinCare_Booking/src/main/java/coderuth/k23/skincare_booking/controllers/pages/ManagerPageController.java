package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterStaffRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterTherapistRequest;
import coderuth.k23.skincare_booking.services.AuthService;
import coderuth.k23.skincare_booking.services.ManagerService;
import coderuth.k23.skincare_booking.models.Manager;
import coderuth.k23.skincare_booking.repositories.ManagerRepository;
import coderuth.k23.skincare_booking.services.StaffService;
import coderuth.k23.skincare_booking.services.TherapistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/protected/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerPageController {
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private TherapistService therapistService;

    @Autowired
    private AuthService authService;

    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/home")
    public String adminPage(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
        return "admin/index"; // "user/index.html"
    }

    @GetMapping("/profile")
    public String getManagerProfile(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
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
            managerService.updateManagerProfile(username, profileRequest);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }

        // Redirect về trang profile
        return "redirect:/protected/manager/profile";
    }

    @GetMapping("/security")
    public String getSecurity(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
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
            managerService.changePassword(username, currentPassword, newPassword, confirmPassword);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            // Thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }

        return "redirect:/protected/manager/security";
    }

    @GetMapping("/list-staff")
    public String getStaffList(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
        model.addAttribute("staffList", staffService.getStaffList());
        return "admin/Users/ListStaff/listStaff";
    }

    @GetMapping("/list-therapist")
    public String getTherapistList(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
        model.addAttribute("therapistList", therapistService.getAllTherapists());
        return "admin/Users/ListTherapist/listTherapist";
    }

    @PostMapping("/register-staff")
    public String registerStaff(@ModelAttribute RegisterStaffRequest registerStaffRequest, RedirectAttributes redirectAttributes) {
        try {
            // Cập nhật thông tin hồ sơ
            authService.registerStaff(registerStaffRequest);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Registered successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to registered: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-staff";
    }

    @PostMapping("/register-therapist")
    public String registerStaff(@ModelAttribute RegisterTherapistRequest registerTherapistRequest, RedirectAttributes redirectAttributes) {
        try {
            // Cập nhật thông tin hồ sơ
            authService.registerSkinTherapist(registerTherapistRequest);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Registered successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to registered: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-therapist";
    }

    @GetMapping("/spa-services")
    public String spaServicesPage() {
        return "redirect:/spa-services"; 
    }

    @GetMapping("/spa-services/schedules")
    public String centerSchedulesPage() {
        return "redirect:/spa-services/schedules"; 
    }


    @GetMapping("/therapists")
    public String TherapistPage() {
        return "redirect:/therapists"; 
    }

    @GetMapping("/appointments/pending")
    public String appointmentsPage() {
        return "redirect:/protected/staff/appointments/pending"; 
    }
    
}
