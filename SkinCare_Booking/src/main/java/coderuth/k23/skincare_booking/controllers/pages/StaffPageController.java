package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.BlogRequestDTO;
import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.dtos.response.BlogResponseDTO;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.StaffRepository;
import coderuth.k23.skincare_booking.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/protected/staff")
@PreAuthorize("hasRole('STAFF')")
public class StaffPageController {

    @Autowired
    StaffService staffService;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    private BlogService blogService;

    @Autowired
    TherapistService therapistService;

    @Autowired
    CustomerService customerService;

    @Autowired
    SpaServiceService spaServiceService;

    @Autowired
    private CenterScheduleService centerScheduleService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PaymentService paymentService;

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

    @GetMapping("/blogs")
    public String getBlogList(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        model.addAttribute("role", "staff");
        model.addAttribute("staff", staff);
        List<BlogResponseDTO> blogList = blogService.getAllBlogs();
        model.addAttribute("blogs", blogList);
        return "admin/Blog/blog_management";
    }

    @PostMapping("/create-blog")
    public String createBlog(@ModelAttribute BlogRequestDTO blogRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            blogService.createBlog(blogRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Blog created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create blog: " + e.getMessage());
        }
        return "redirect:/protected/staff/blogs";
    }

    @PostMapping("/update-blog/{id}")
    public String updateBlog(@PathVariable Long id,
                             @ModelAttribute BlogRequestDTO blogRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            blogService.updateBlog(id, blogRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Blog updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update blog: " + e.getMessage());
        }
        // Redirect về list đúng route
        return "redirect:/protected/staff/blogs";
    }


    @GetMapping("/blogs/delete/{id}")
    public String deleteBlog(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            blogService.deleteBlog(id);
            redirectAttributes.addFlashAttribute("successMessage", "Blog deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete blog: " + e.getMessage());
        }
        // Redirect về list đúng route
        return "redirect:/protected/staff/blogs";
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
    // Appointment endpoints for Staff
    @GetMapping("/appointments")
    public String listAllAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        List<Appointment> appointments = appointmentService.getAllAppointments();
        System.out.println("Số lượng lịch hẹn: " + appointments.size()); // Log để kiểm tra
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointments);
        return "admin/staff/listAppointments";
    }

    @GetMapping("/appointments/pending")
    public String listPendingAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.PENDING));
        return "admin/staff/pending_appointments";
    }

    @PostMapping("/appointments/check-in/{id}")
    public String checkIn(@PathVariable Long id) {
        appointmentService.checkIn(id);
        return "redirect:/protected/staff/appointments/checked-in";
    }

    @GetMapping("/appointments/checked-in")
    public String listCheckedInAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_IN));
        return "admin/staff/checked_in_appointments";
    }

    @GetMapping("/appointments/assign/{id}")
    public String showAssignTherapistForm(@PathVariable Long id, Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        Appointment appointment = appointmentService.getAppointmentById(id);

        if (appointment.getStatus() == Appointment.AppointmentStatus.ASSIGNED) {
            return "redirect:/protected/staff/appointments/assigned";
        }
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_IN) {
            model.addAttribute("error", "Lịch hẹn phải ở trạng thái CHECKED_IN để phân công!");
            model.addAttribute("staff", staff);
            model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_IN));
            return "admin/staff/checked_in_appointments";
        }

        // Lấy danh sách tất cả chuyên viên
        List<SkinTherapist> allTherapists = therapistService.getAllTherapists();
        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        SpaService spaService = appointment.getSpaService();

        // Kiểm tra spaService có null không
        if (spaService == null) {
            model.addAttribute("error", "Dịch vụ không được tìm thấy cho lịch hẹn này!");
            model.addAttribute("staff", staff);
            model.addAttribute("appointment", appointment);
            model.addAttribute("therapists", allTherapists);
            return "admin/staff/assign_therapist";
        }

        LocalDateTime serviceEndTime = appointmentTime.plusMinutes(spaService.getDuration());

        // Lọc chuyên viên không có lịch hẹn trùng thời gian
        List<SkinTherapist> availableTherapists = allTherapists.stream()
                .filter(therapist -> {
                    List<Appointment> existingAppointments = appointmentService.getAppointmentsByTherapist(therapist.getId());
                    return !existingAppointments.stream().anyMatch(a -> {
                        LocalDateTime existingStart = a.getAppointmentTime();
                        SpaService existingService = a.getSpaService();
                        // Kiểm tra null cho existingService
                        if (existingService == null) {
                            return false; // Bỏ qua nếu không có dịch vụ
                        }
                        LocalDateTime existingEnd = existingStart.plusMinutes(existingService.getDuration());
                        return a.getStatus() != Appointment.AppointmentStatus.CANCELLED &&
                                appointmentTime.isBefore(existingEnd) && serviceEndTime.isAfter(existingStart);
                    });
                })
                .collect(Collectors.toList());

        model.addAttribute("staff", staff);
        model.addAttribute("appointment", appointment);
        model.addAttribute("therapists", availableTherapists);
        return "admin/staff/assign_therapist";
    }

    @PostMapping("/appointments/assign/{id}")
    public String assignTherapist(@PathVariable Long id, @RequestParam UUID therapistId, RedirectAttributes redirectAttributes, Model model, Principal principal) {
        try {
            appointmentService.assignTherapist(id, therapistId);
            return "redirect:/protected/staff/appointments/assigned";
        } catch (RuntimeException e) {
            String username = principal.getName();
            Staff staff = staffRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            Appointment appointment = appointmentService.getAppointmentById(id);

            if (appointment.getStatus() == Appointment.AppointmentStatus.ASSIGNED) {
                return "redirect:/protected/staff/appointments/assigned";
            }
            if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_IN) {
                redirectAttributes.addFlashAttribute("error", "Lịch hẹn phải ở trạng thái CHECKED_IN để phân công!");
                return "redirect:/protected/staff/appointments/checked-in";
            }

            model.addAttribute("staff", staff);
            model.addAttribute("appointment", appointment);
            model.addAttribute("therapists", therapistService.getAllTherapists());
            model.addAttribute("error", e.getMessage());
            return "admin/staff/assign_therapist";
        }
    }

    @GetMapping("/appointments/assigned")
    public String listAssignedAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED));
        return "admin/staff/assigned_appointments";
    }

    @GetMapping("/appointments/completed")
    public String listCompletedAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.COMPLETED));
        return "admin/staff/completed_appointments";
    }

    @PostMapping("/appointments/check-out/{id}")
    public String checkOut(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model, Principal principal) {
        try {
            appointmentService.checkOut(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment checked out successfully!");
            return "redirect:/protected/staff/appointments/checked-out";
        } catch (RuntimeException e) {
            String username = principal.getName();
            Staff staff = staffRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            model.addAttribute("staff", staff);
            model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.COMPLETED));
            model.addAttribute("error", e.getMessage());
            return "admin/staff/completed_appointments";
        }
    }

    @GetMapping("/appointments/checked-out")
    public String listCheckedOutAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_OUT));
        return "admin/staff/checked_out_appointments";
    }

    @GetMapping("/appointments/invoice/{id}")
    public String showInvoice(@PathVariable Long id, Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Lịch hẹn không ở trạng thái CHECKED_OUT!");
        }
        Payment payment = appointment.getPayment();
        boolean isUnpaid = payment == null || payment.getPaymentStatus() != Payment.PaymentStatus.PAID;
        model.addAttribute("staff", staff);
        model.addAttribute("appointment", appointment);
        model.addAttribute("isUnpaid", isUnpaid);
        return "admin/staff/invoice";
    }

    @PostMapping("/appointments/confirm-payment/{id}")
    public String confirmPayment(@PathVariable Long id) {
        appointmentService.confirmPayment(id);
        return "redirect:/protected/staff/appointments/invoice/" + id;
    }

    @GetMapping("/appointments/record-result/{id}")
    public String showRecordResultForm(@PathVariable Long id, Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            if (appointment.getStatus() != Appointment.AppointmentStatus.ASSIGNED) {
                model.addAttribute("error", "Lịch hẹn phải ở trạng thái ASSIGNED để ghi kết quả! Trạng thái hiện tại: " + appointment.getStatus());
                model.addAttribute("staff", staff);
                model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED));
                return "admin/staff/assigned_appointments";
            }
            model.addAttribute("staff", staff);
            model.addAttribute("appointment", appointment);
            return "admin/therapists/therapists_record_result";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("staff", staff);
            model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED));
            return "admin/staff/assigned_appointments";
        }
    }

    @PostMapping("/appointments/record-result/{id}")
    public String recordResult(@PathVariable Long id, @RequestParam String result, Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        try {
            if (result == null || result.trim().isEmpty()) {
                throw new IllegalArgumentException("Kết quả không được để trống!");
            }
            Appointment appointment = appointmentService.getAppointmentById(id);
            appointmentService.recordResult(id, result);
            return "redirect:/protected/staff/appointments/completed";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("staff", staff);
            model.addAttribute("appointment", appointmentService.getAppointmentById(id));
            return "admin/therapists/therapists_record_result";
        }
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model, Principal principal) {
        try {
            appointmentService.cancelAppointmentByStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment canceled successfully!");
            return "redirect:/protected/staff/appointments/pending";
        } catch (RuntimeException e) {
            String username = principal.getName();
            Staff staff = staffRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            model.addAttribute("staff", staff);
            model.addAttribute("appointments", appointmentService.getAllAppointments());
            model.addAttribute("errorMessage", e.getMessage()); // Truyền thông báo lỗi
            return "admin/staff/listAppointments"; // Trả về trang danh sách lịch hẹn với thông báo lỗi
        }
    }

    @GetMapping("/appointments/cancelled")
    public String listCancelledAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CANCELLED));
        return "admin/staff/cancelled_appointments";
    }

    @GetMapping("/payments/history")
    public String viewPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model, Principal principal) {
        try {
            String username = principal.getName();
            Staff staff = staffRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            Page<Payment> paymentPage = paymentService.findAllPayments(page, size);
            model.addAttribute("staff", staff);
            model.addAttribute("payments", paymentPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", paymentPage.getTotalPages());
            model.addAttribute("totalItems", paymentPage.getTotalElements());
            return "admin/staff/staff_payment_history";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to load payment history: " + e.getMessage());
            return "error";
        }
    }

    // Cập nhật lịch hẹn
    @PostMapping("/appointments/update/{id}")
    public String updateAppointment(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String result,
            RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            appointment.setStatus(Appointment.AppointmentStatus.valueOf(status));
            if (result != null && !result.trim().isEmpty()) {
                appointment.setResult(result);
            }
            appointmentService.updateAppointment(appointment);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật lịch hẹn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật lịch hẹn: " + e.getMessage());
        }
        return "redirect:/protected/staff/appointments";
    }
}

