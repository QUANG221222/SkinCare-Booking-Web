package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.services.CenterScheduleService;
import coderuth.k23.skincare_booking.services.SpaServiceService;
import coderuth.k23.skincare_booking.services.TherapistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/protected/therapist")
@PreAuthorize("hasRole('THERAPIST')")
public class SkinTherapistPageController {

    @Autowired
    TherapistService therapistService;

    @Autowired
    SkinTherapistRepository skinTherapistRepository;

    @Autowired
    SpaServiceService spaServiceService;

    @Autowired
    private CenterScheduleService centerScheduleService;

    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/home")
    public String therapistPage(Model model, Principal principal) {
        String username = principal.getName();
        SkinTherapist therapist = skinTherapistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        model.addAttribute("therapist", therapist);
        return "admin/index";
    }

    @GetMapping("/profile")
    public String getTherapistProfile(Model model, Principal principal) {
        String username = principal.getName();
        SkinTherapist therapist = skinTherapistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        model.addAttribute("therapist", therapist);
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
            SkinTherapist therapist = skinTherapistRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Therapist not found"));
            therapist.setFullName(profileRequest.getFullName());
            therapist.setImg(profileRequest.getImg());
            therapist.setPhone(profileRequest.getPhone());
            therapist.setLocation(profileRequest.getLocation());
            therapistService.updateTherapist(therapist.getId(), therapist);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }

        // Redirect về trang profile
        return "redirect:/protected/therapist/profile";
    }

    @GetMapping("/security")
    public String getSecurity(Model model, Principal principal) {
        String username = principal.getName();
        SkinTherapist therapist = skinTherapistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        model.addAttribute("therapist", therapist);
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
            therapistService.changePassword(username, currentPassword, newPassword, confirmPassword);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            // Thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }

        return "redirect:/protected/therapist/security";
    }

    @GetMapping("/spa-services")
    public String spaServicesPage(Model model, Principal principal) {
        String username = principal.getName();
        SkinTherapist therapist = skinTherapistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        model.addAttribute("therapist", therapist);
        model.addAttribute("spaServicesList", spaServiceService.getAllServices());
        return "/admin/SpaServices/ListSpaServices/listSpaServices";
    }

    @GetMapping("/center-schedule")
    public String getCenterSchedule(Model model, Principal principal) {
        String username = principal.getName();
        SkinTherapist therapist = skinTherapistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        model.addAttribute("therapist", therapist);
        model.addAttribute("scheduleList", centerScheduleService.getAllSchedules());
        return "admin/Schedules/centerSchedule";
    }
}