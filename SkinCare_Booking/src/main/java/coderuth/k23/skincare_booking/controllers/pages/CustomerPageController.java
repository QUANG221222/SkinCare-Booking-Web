package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import coderuth.k23.skincare_booking.services.*;
import coderuth.k23.skincare_booking.dtos.response.BlogResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private FeedbackService feedbackService;

    @Autowired
    private SpaServiceService spaServiceService;

    @Autowired
    SpaServiceRepository spaServiceRepository;

    @Autowired
    private TherapistService therapistService;

    @Autowired
    private BlogService blogService;


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

//      Hiển thị danh sách dịch vụ và form đặt dịch vụ
     @GetMapping("/appointment")
     public String showAppointmentForm(@RequestParam(value = "serviceId", required = false) Long serviceId, Model model) {
         Appointment appointment = new Appointment();
         if (serviceId != null) {
             SpaService service = spaServiceService.getServiceById(serviceId);
             appointment.setSpaService(service);
         }
         model.addAttribute("appointment", appointment);
         // Nạp thêm danh sách dịch vụ và chuyên viên nếu cần cho form lựa chọn thay đổi (hoặc chỉ nạp nếu chưa có service chọn trước)
         model.addAttribute("services", spaServiceService.getAllServices());
         model.addAttribute("therapists", therapistService.getAllTherapists());
         return "user/customer/appointment_form";
     }
    // Customer books a service
    @PostMapping("/appointment/create")
    @ResponseBody
    public ResponseEntity<?> createAppointment(@ModelAttribute Appointment appointment) {
        try {
            UUID customerId = getLoggedInCustomerId();
           
            // Retrieve SpaService from the database
            Long serviceId = appointment.getSpaService().getId();
            SpaService spaService = spaServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service does not exist with ID: " + serviceId));

            // Assign the full SpaService to the appointment
            appointment.setSpaService(spaService);
           
            // Assign customer
            Customer customer = new Customer();
            customer.setId(customerId);
            appointment.setCustomer(customer);

            // Check if a SkinTherapist is assigned
            if (appointment.getSkinTherapist() != null && appointment.getSkinTherapist().getId() != null) {
                // Retrieve SkinTherapist from the database
                SkinTherapist therapist = therapistService.getTherapistById(appointment.getSkinTherapist().getId());
                if (therapist == null) {
                    throw new IllegalArgumentException("Therapist does not exist with ID: " + appointment.getSkinTherapist().getId());
                }
                appointment.setSkinTherapist(therapist);
            } else {
                // If no SkinTherapist is selected, set the value to null
                appointment.setSkinTherapist(null);
            }

            appointmentService.createAppointment(appointment);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Service booked successfully!"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", ex.getMessage()));
        }
    }
  
    // Xem danh sách đặt dịch vụ của khách hàng
    // @GetMapping("/appointments")
    // public String getAppointmentsFragment(Model model, Authentication authentication) {
    //     String username = authentication.getName();
    //     List<Appointment> appointments = appointmentService.findAppointmentsByUsername(username);
    //     model.addAttribute("appointments", appointments);
    //     return "user/customer/appointments_list :: appointmentsList";
    // }
   // Xem danh sách đặt dịch vụ của khách hàng
//   @GetMapping("/appointments")
//   public String listAppointments(Model model) {
//       UUID customerId = getLoggedInCustomerId();
//       model.addAttribute("appointments", appointmentService.getAppointmentsByCustomer(customerId));
//       return "user/customer/appointments_list";
//   }

//     // Hủy lịch hẹn
//     @PostMapping("/appointments/cancel/{id}")
//     public String cancelAppointment(@PathVariable Long id) {
//         UUID customerId = getLoggedInCustomerId();
//         appointmentService.cancelAppointmentByCustomer(id, customerId);
//         return "redirect:/protected/customer/appointments";
//     }

   // Xem lịch sử thanh toán của customer
    @GetMapping("/payment-history")
    public String viewPaymentHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        UUID customerId;
        try {
            customerId = UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid customer ID!");
            return "error";
        }


        try {
            Page<Payment> paymentPage = appointmentService.getPaymentHistoryByCustomerIdAndFilters(
                    customerId, page, size, status, startDate, endDate);
            model.addAttribute("payments", paymentPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", paymentPage.getTotalPages());
            model.addAttribute("totalItems", paymentPage.getTotalElements());
            model.addAttribute("status", status);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }


        return "user/customer/customer_payment_history";
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
    public String userBlogPage(Model model) {
        List<BlogResponseDTO> blogs = blogService.getAllBlogs();
        model.addAttribute("blogs", blogs);
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

    @GetMapping("/appointments")
    public String bookingHistory(Model model,Authentication authentication) {
        // Lấy lịch sử đặt lịch từ cơ sở dữ liệu
        String username = authentication.getName();
        List<Appointment> appointments = appointmentService.findAppointmentsByUsername(username);
        model.addAttribute("appointments", appointments);
        return "user/customer/booking-history";
    }

    @PostMapping("/appointments/cancel/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id) {
        try {
            UUID customerId = getLoggedInCustomerId();
            appointmentService.cancelAppointmentByCustomer(id, customerId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "\n" + "Appointment canceled successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/quiz")
    public String showQuiz() {
        return "user/customer/quiz";
    }

    //hiển thị lịch làm việc của trung tâm
    @Autowired
    private CenterScheduleService centerScheduleService;

    @ModelAttribute
    public void addCenterSchedules(Model model) {
        model.addAttribute("centerSchedules", centerScheduleService.getAllSchedules());
    }
    // Xem danh sách lịch hẹn đã hủy của khách hàng
    @GetMapping("/appointments/cancelled")
    public String listCancelledAppointments(Model model) {
        UUID customerId = getLoggedInCustomerId();
        List<Appointment> cancelledAppointments = appointmentService.getAppointmentsByCustomer(customerId)
                .stream()
                .filter(appointment -> appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED)
                .toList();
        model.addAttribute("appointments", cancelledAppointments);
        return "user/customer/customer_cancelled_appointments";
    }
}
