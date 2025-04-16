package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterStaffRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterTherapistRequest;
import coderuth.k23.skincare_booking.dtos.request.SpaServiceRequestDTO;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.Staff;
import coderuth.k23.skincare_booking.services.*;
import coderuth.k23.skincare_booking.models.Manager;
import coderuth.k23.skincare_booking.repositories.ManagerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.UUID;

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
    private CustomerService customerService;

    @Autowired
    private AuthService authService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private SpaServiceService spaServiceService;

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

    @GetMapping("/delete-staff/{id}")
    public String deleteStaff(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Staff has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the staff: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-staff";
    }

    @PostMapping("/update-staff/{id}")
    public String updateStaff(@PathVariable UUID id, @ModelAttribute Staff staff, RedirectAttributes redirectAttributes) {
        try {
            staffService.updateStaff(id, staff);
            redirectAttributes.addFlashAttribute("successMessage", "Staff has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the staff: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-staff";
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

    @GetMapping("/delete-therapist/{id}")
    public String deleteTherapist(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            therapistService.deleteTherapist(id);
            redirectAttributes.addFlashAttribute("successMessage", "Therapist has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the therapist: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-therapist";
    }

    @PostMapping("/update-therapist/{id}")
    public String updateTherapist(@PathVariable UUID id, @ModelAttribute SkinTherapist therapist, RedirectAttributes redirectAttributes) {
        try {
            therapistService.updateTherapist(id, therapist);
            redirectAttributes.addFlashAttribute("successMessage", "Therapist has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the therapist: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-therapist";
    }

    @GetMapping("/list-customer")
    public String getCustomerList(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
        model.addAttribute("customerList", customerService.getAllCustomers());
        return "admin/Users/ListCustomer/listCustomer";
    }

    @PostMapping("/update-customer/{id}")
    public String updateCustomer(@PathVariable UUID id, @ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            customerService.updateCustomer(id, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Customer has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the customer: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-customer";
    }

    @GetMapping("/delete-customer/{id}")
    public String deleteCustomer(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the customer: " + e.getMessage());
        }
        return "redirect:/protected/manager/list-customer";
    }

    @GetMapping("/spa-services")
    public String spaServicesPage(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
        model.addAttribute("spaServicesList", spaServiceService.getAllServices());
        System.out.println(spaServiceService.getAllServices());
        return "/admin/SpaServices/ListSpaServices/listSpaServices";
    }

    @PostMapping("/add-services")
    public String addNewServices(@ModelAttribute SpaServiceRequestDTO spaServiceRequestDTO, RedirectAttributes redirectAttributes) {
        try {

            //Add new
            spaServiceService.createService(spaServiceRequestDTO);

            redirectAttributes.addFlashAttribute("successMessage", "Service has been created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while creating the service: " + e.getMessage());
        }
        return "redirect:/protected/manager/spa-services";
    }

    @GetMapping("/spa-services/schedules")
    public String centerSchedulesPage() {
        return "redirect:/spa-services/schedules"; 
    }


    @GetMapping("/therapists")
    public String TherapistPage() {
        return "redirect:/therapists"; 
    }

    //endpoint quản lí feedback
    @GetMapping("/feedbacks")
    public String getFeedbackList(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
        model.addAttribute("feedbackList", feedbackService.getAllFeedbacks(username));
        return "admin/Feedbacks/managerFeedback";
    }
}
