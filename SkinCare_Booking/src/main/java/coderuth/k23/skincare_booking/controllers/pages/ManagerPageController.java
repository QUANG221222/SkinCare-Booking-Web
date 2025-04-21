package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.BlogRequestDTO;
import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterStaffRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterTherapistRequest;
import coderuth.k23.skincare_booking.dtos.response.BlogResponseDTO;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.dtos.request.SpaServiceRequestDTO;
import coderuth.k23.skincare_booking.repositories.AppointmentRepository;
import coderuth.k23.skincare_booking.services.*;
import coderuth.k23.skincare_booking.repositories.ManagerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/protected/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerPageController {
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private BlogService blogService;

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
    private CenterScheduleService centerScheduleService;

    @Autowired
    private SpaServiceService spaServiceService;

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
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        long totalAppointments = appointmentService.getTotalAppointments();
        double cancellationRate = appointmentService.getCancellationRate();
        double successRate = appointmentService.getSuccessRate();
        SpaService mostBookedSpaService = appointmentService.getMostBookedSpaService();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
        String monthName = LocalDateTime.now().format(formatter);

        model.addAttribute("manager", manager);
        model.addAttribute("month", monthName);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("cancellationRate", String.format("%.2f", cancellationRate));
        model.addAttribute("successRate", String.format("%.2f", successRate));
        model.addAttribute("mostBookedSpaService", mostBookedSpaService != null ? mostBookedSpaService.getName() : "N/A");
        model.addAttribute("sales", appointmentService.calculateCurrentMonthRevenue());
        model.addAttribute("annualRevenue", appointmentService.calculateCurrentYearRevenue());
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

    @GetMapping("/center-schedule")
    public String getCenterSchedule(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        model.addAttribute("manager", manager);
        model.addAttribute("scheduleList", centerScheduleService.getAllSchedules());
        return "admin/Schedules/centerSchedule";
    }

    @PostMapping("/center-schedule/add")
    public String addCenterSchedule(@ModelAttribute CenterSchedule schedule, RedirectAttributes redirectAttributes) {
        try {
            centerScheduleService.createSchedule(schedule);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add schedule: " + e.getMessage());
        }
        return "redirect:/protected/manager/center-schedule";
    }

    @PostMapping("/center-schedule/update/{id}")
    public String updateCenterSchedule(@PathVariable Long id, @ModelAttribute CenterSchedule schedule, RedirectAttributes redirectAttributes) {
        try {
            centerScheduleService.updateSchedule(id, schedule);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update schedule: " + e.getMessage());
        }
        return "redirect:/protected/manager/center-schedule";
    }

    @GetMapping("/center-schedule/delete/{id}")
    public String deleteCenterSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            centerScheduleService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete schedule: " + e.getMessage());
        }
        return "redirect:/protected/manager/center-schedule";
    }

    @GetMapping("/delete-service/{id}")
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra xem dịch vụ có đang được sử dụng trong lịch hẹn không
            Optional<SpaService> spaServiceOptional = spaServiceService.getAllServices().stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst();

            if (spaServiceOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Service not found to delete.");
                return "redirect:/protected/manager/spa-services";
            }

            spaServiceService.deleteService(id);

            redirectAttributes.addFlashAttribute("successMessage", "Spa Service has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the spa service: " + e.getMessage());
        }
        return "redirect:/protected/manager/spa-services";
    }

    @PostMapping("/update-service/{id}")
    public String updateService(@PathVariable Long id, @ModelAttribute SpaService spaService, RedirectAttributes redirectAttributes) {
        try {
            spaServiceService.updateService(id, spaService);

            redirectAttributes.addFlashAttribute("successMessage", "Service has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the service: " + e.getMessage());
        }
        return "redirect:/protected/manager/spa-services";
    }

    @GetMapping("/spa-services/schedules")
    public String centerSchedulesPage() {
        return "redirect:/spa-services/schedules";
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
     // Appointment endpoints for Manager
     @GetMapping("/appointments")
     public String listAllAppointments(Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Không tìm thấy quản lý"));
         List<Appointment> appointments = appointmentService.getAllAppointments();
         System.out.println("Số lượng lịch hẹn: " + appointments.size()); // Log để kiểm tra
         model.addAttribute("manager", manager);
         model.addAttribute("appointments", appointmentService.getAllAppointments());
         return "admin/staff/listAppointments";
     }

    @GetMapping("/appointments/pending")
     public String listPendingAppointments(Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
         model.addAttribute("manager", manager);
         model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.PENDING));
         return "admin/staff/pending_appointments";
     }
 
     @PostMapping("/appointments/check-in/{id}")
     public String checkIn(@PathVariable Long id) {
         appointmentService.checkIn(id);
         return "redirect:/protected/manager/appointments/checked-in";
     }
 
     @GetMapping("/appointments/checked-in")
     public String listCheckedInAppointments(Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
         model.addAttribute("manager", manager);
         model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_IN));
         return "admin/staff/checked_in_appointments";
     }

    @GetMapping("/appointments/assign/{id}")
    public String showAssignTherapistForm(@PathVariable Long id, Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        Appointment appointment = appointmentService.getAppointmentById(id);

        if (appointment.getStatus() == Appointment.AppointmentStatus.ASSIGNED) {
            return "redirect:/protected/manager/appointments/assigned";
        }
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_IN) {
            model.addAttribute("error", "Lịch hẹn phải ở trạng thái CHECKED_IN để phân công!");
            model.addAttribute("manager", manager);
            model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_IN));
            return "admin/staff/checked_in_appointments";
        }

        // Lấy danh sách tất cả chuyên viên
        List<SkinTherapist> allTherapists = therapistService.getAllTherapists();
        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        SpaService spaService = appointment.getSpaService();
        LocalDateTime serviceEndTime = appointmentTime.plusMinutes(spaService.getDuration());

        // Lọc chuyên viên không có lịch hẹn trùng thời gian
        List<SkinTherapist> availableTherapists = allTherapists.stream()
                .filter(therapist -> {
                    List<Appointment> existingAppointments = appointmentRepository.findBySkinTherapistId(therapist.getId());
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

        model.addAttribute("manager", manager);
        model.addAttribute("appointment", appointment);
        model.addAttribute("therapists", availableTherapists); // Chỉ hiển thị chuyên viên có sẵn
        return "admin/staff/assign_therapist";
    }

    @PostMapping("/appointments/assign/{id}")
    public String assignTherapist(@PathVariable Long id, @RequestParam UUID therapistId, RedirectAttributes redirectAttributes, Model model, Principal principal) {
        try {
            appointmentService.assignTherapist(id, therapistId);
            return "redirect:/protected/manager/appointments/assigned";
        } catch (RuntimeException e) {
            // Lấy lại thông tin để hiển thị trang assign_therapist.html
            String username = principal.getName();
            Manager manager = managerRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            Appointment appointment = appointmentService.getAppointmentById(id);

            // Kiểm tra trạng thái lịch hẹn
            if (appointment.getStatus() == Appointment.AppointmentStatus.ASSIGNED) {
                return "redirect:/protected/manager/appointments/assigned";
            }
            if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_IN) {
                redirectAttributes.addFlashAttribute("error", "Lịch hẹn phải ở trạng thái CHECKED_IN để phân công!");
                return "redirect:/protected/manager/appointments/checked-in";
            }

            // Thêm thông báo lỗi và dữ liệu cần thiết để render lại trang
            model.addAttribute("manager", manager);
            model.addAttribute("appointment", appointment);
            model.addAttribute("therapists", therapistService.getAllTherapists());
            model.addAttribute("error", e.getMessage()); // Truyền thông báo lỗi
            return "admin/staff/assign_therapist"; // Trả về trang assign_therapist.html
        }
    }
 
     @GetMapping("/appointments/assigned")
     public String listAssignedAppointments(Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
         model.addAttribute("manager", manager);
         model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED));
         return "admin/staff/assigned_appointments";
     }
 
     @GetMapping("/appointments/completed")
     public String listCompletedAppointments(Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
         model.addAttribute("manager", manager);
         model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.COMPLETED));
         return "admin/staff/completed_appointments";
     }
 
     @PostMapping("/appointments/check-out/{id}")
     public String checkOut(@PathVariable Long id) {
         appointmentService.checkOut(id);
         return "redirect:/protected/manager/appointments/checked-out";
     }
 
     @GetMapping("/appointments/checked-out")
     public String listCheckedOutAppointments(Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
         model.addAttribute("manager", manager);
         model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.CHECKED_OUT));
         return "admin/staff/checked_out_appointments";
     }
 
     @GetMapping("/appointments/invoice/{id}")
     public String showInvoice(@PathVariable Long id, Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Lịch hẹn không ở trạng thái CHECKED_OUT!");
        }
         Payment payment = appointment.getPayment();
         boolean isUnpaid = payment == null || payment.getPaymentStatus() != Payment.PaymentStatus.PAID;
         model.addAttribute("manager", manager);
         model.addAttribute("appointment", appointment);
         model.addAttribute("isUnpaid", isUnpaid);
         return "admin/staff/invoice";
     }
 
     @PostMapping("/appointments/confirm-payment/{id}")
     public String confirmPayment(@PathVariable Long id) { // Bỏ tham số paymentMethod
         appointmentService.confirmPayment(id);
         return "redirect:/protected/staff/appointments/invoice/{id}";
     }
     
     @GetMapping("/appointments/record-result/{id}")
     public String showRecordResultForm(@PathVariable Long id, Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
         try {
             Appointment appointment = appointmentService.getAppointmentById(id);
             if (appointment.getStatus() != Appointment.AppointmentStatus.ASSIGNED) {
                 model.addAttribute("error", "Lịch hẹn phải ở trạng thái ASSIGNED để ghi kết quả! Trạng thái hiện tại: " + appointment.getStatus());
                 model.addAttribute("manager", manager);
                 model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED));
                 return "admin/staff/assigned_appointments";
             }
             model.addAttribute("manager", manager);
             model.addAttribute("appointment", appointment);
             return "admin/therapists/therapists_record_result";
         } catch (RuntimeException e) {
             model.addAttribute("error", e.getMessage());
             model.addAttribute("manager", manager);
             model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED));
             return "admin/staff/assigned_appointments";
         }
     }
 
     @PostMapping("/appointments/record-result/{id}")
     public String recordResult(@PathVariable Long id, @RequestParam String result, Model model, Principal principal) {
         String username = principal.getName();
         Manager manager = managerRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("Manager not found"));
         try {
             if (result == null || result.trim().isEmpty()) {
                 throw new IllegalArgumentException("Kết quả không được để trống!");
             }
             Appointment appointment = appointmentService.getAppointmentById(id);
             appointmentService.recordResult(id, result);
             return "redirect:/protected/manager/appointments/completed";
         } catch (RuntimeException e) {
             model.addAttribute("error", e.getMessage());
             model.addAttribute("manager", manager);
             model.addAttribute("appointment", appointmentService.getAppointmentById(id));
             return "admin/therapists/therapists_record_result";
         }
     }
 
     @PostMapping("/appointments/cancel/{id}")
     public String cancelAppointment(@PathVariable Long id) {
         appointmentService.cancelAppointmentByStaff(id);
         return "redirect:/protected/manager/appointments/pending";
     }
 
    @GetMapping("/appointments/cancelled")
    public String listCancelledAppointments(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quản lý"));
        model.addAttribute("manager", manager);
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
            Manager manager = managerRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            Page<Payment> paymentPage = paymentService.findAllPayments(page, size);
            model.addAttribute("manager", manager);
            model.addAttribute("payments", paymentPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", paymentPage.getTotalPages());
            model.addAttribute("totalItems", paymentPage.getTotalElements());
            return "admin/staff/staff_payment_history";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to load payment history: " + e.getMessage());
            return "error"; // Hoặc trả về một template lỗi
        }
    }


    // Display the Blog management page with list of blogs
    @GetMapping("/blogs")
    public String getBlogList(Model model, Principal principal) {
        String username = principal.getName();
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        model.addAttribute("manager", manager);
        List<BlogResponseDTO> blogList = blogService.getAllBlogs();
        model.addAttribute("blogs", blogList);
        return "admin/Blog/blog_management";
    }

    // Create a new blog
    @PostMapping("/create-blog")
    public String createBlog(@ModelAttribute BlogRequestDTO blogRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            blogService.createBlog(blogRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Blog created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create blog: " + e.getMessage());
        }
        return "redirect:/protected/manager/blogs";
    }

    // Update an existing blog
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
        return "redirect:/protected/manager/blogs";
    }

    // Delete a blog
    @GetMapping("/blogs/delete/{id}")
    public String deleteBlog(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            blogService.deleteBlog(id);
            redirectAttributes.addFlashAttribute("successMessage", "Blog deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete blog: " + e.getMessage());
        }
        return "redirect:/protected/manager/blogs";
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
        return "redirect:/protected/manager/appointments";
    }

   // Xem lịch làm việc của chuyên viên
    @GetMapping("/therapists/{id}/schedules/current")
    @ResponseBody
    public ResponseEntity<List<TherapistSchedule>> getTherapistSchedules(@PathVariable UUID id) {
        try {
            List<TherapistSchedule> schedules = therapistService.getSchedulesByTherapist(id);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Tạo lịch làm việc cho chuyên viên
    @PostMapping("/therapists/{id}/schedules")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTherapistSchedule(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String dayOfWeek = request.get("dayOfWeek");
            String startTime = request.get("startTime");
            String endTime = request.get("endTime");

            therapistService.createSchedule(id, dayOfWeek, startTime, endTime);
            response.put("therapistId", id.toString());
            response.put("message", "Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("therapistId", id.toString());
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    // Cập nhật lịch làm việc cho chuyên viên
    @PostMapping("/therapists/schedules/update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTherapistSchedule(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String dayOfWeek = request.get("dayOfWeek");
            String startTime = request.get("startTime");
            String endTime = request.get("endTime");
            String therapistId = request.get("therapistId");

            therapistService.updateSchedule(id, dayOfWeek, startTime, endTime);
            response.put("therapistId", therapistId);
            response.put("message", "Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("therapistId", request.get("therapistId"));
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
    @DeleteMapping("/therapists/schedules/delete/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable("scheduleId") UUID scheduleId) {
        System.out.println("Received DELETE request for schedule ID: " + scheduleId);
        try {
            therapistService.deleteSchedule(scheduleId);
            return ResponseEntity.ok("Schedule deleted successfully");
        } catch (Exception e) {
            System.out.println("Error deleting schedule: " + e.getMessage());
            return ResponseEntity.status(500).body("Error deleting schedule: " + e.getMessage());
        }
    }
}

