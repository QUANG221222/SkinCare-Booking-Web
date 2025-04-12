package coderuth.k23.skincare_booking.controllers.res;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.TherapistSchedule;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;
import coderuth.k23.skincare_booking.services.AppointmentService;
import coderuth.k23.skincare_booking.models.Appointment;
import coderuth.k23.skincare_booking.services.TherapistService;

import java.util.UUID;

@Controller
@RequestMapping("/therapists")
public class TherapistController {

    @Autowired
    private TherapistService therapistService;

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    @Autowired
    private TherapistScheduleRepository therapistScheduleRepository;
    
    @Autowired
    private AppointmentService appointmentService;

    // Quản lý nhà trị liệu
    @GetMapping
    public String listTherapists(Model model) {
        model.addAttribute("therapists", therapistService.getAllTherapists());
        return "admin/therapists/therapists_list"; 
    }

    @GetMapping("/new")
    public String showCreateTherapistForm(Model model) {
        model.addAttribute("therapist", new SkinTherapist());
        return "admin/therapists/therapists_create";
    }

    @PostMapping
    public String createTherapist(@ModelAttribute SkinTherapist therapist, RedirectAttributes redirectAttributes) {
        try {
            therapistService.createTherapist(therapist);
            redirectAttributes.addFlashAttribute("success", "Therapist has been created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the therapist: " + e.getMessage());
        }
        return "redirect:/therapists";
    }

    @GetMapping("/edit/{id}")
    public String showEditTherapistForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            SkinTherapist therapist = therapistService.getAllTherapists().stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Therapist not found!"));
            model.addAttribute("therapist", therapist);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while retrieving the therapist: " + e.getMessage());
            return "redirect:/therapists";
        }
        return "admin/therapists/therapists_edit";
    }

    @PostMapping("/update/{id}")
    public String updateTherapist(@PathVariable UUID id, @ModelAttribute SkinTherapist therapist, RedirectAttributes redirectAttributes) {
        try {
            therapistService.updateTherapist(id, therapist);
            redirectAttributes.addFlashAttribute("success", "Therapist has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the therapist: " + e.getMessage());
        }
        return "redirect:/therapists";
    }

    @GetMapping("/delete/{id}")
    public String deleteTherapist(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            therapistService.deleteTherapist(id);
            redirectAttributes.addFlashAttribute("success", "Therapist has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the therapist: " + e.getMessage());
        }
        return "redirect:/therapists";
    }

    // Quản lý lịch làm việc của nhà trị liệu
    @GetMapping("/{therapistId}/schedules")
    public String listSchedulesByTherapist(@PathVariable UUID therapistId, Model model, RedirectAttributes redirectAttributes) {
        try {
            SkinTherapist therapist = skinTherapistRepository.findById(therapistId)
                    .orElseThrow(() -> new RuntimeException("Therapist not found!"));
            model.addAttribute("therapist", therapist);
            model.addAttribute("schedules", therapistService.getSchedulesByTherapist(therapistId));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while retrieving schedules: " + e.getMessage());
            return "redirect:/therapists";
        }
        return "admin/therapists-schedules/therapists-schedules_list";
    }

    @GetMapping("/{therapistId}/schedules/new")
    public String showCreateScheduleForm(@PathVariable UUID therapistId, Model model) {
        SkinTherapist therapist = skinTherapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found!"));
        TherapistSchedule schedule = new TherapistSchedule();
        schedule.setSkinTherapist(therapist);
        model.addAttribute("schedule", schedule);
        return "admin/therapists-schedules/therapists-schedules_create";
    }

    @PostMapping("/schedules")
    public String createSchedule(@ModelAttribute TherapistSchedule schedule, RedirectAttributes redirectAttributes) {
        try {
            therapistService.createSchedule(schedule);
            redirectAttributes.addFlashAttribute("success", "Schedule has been created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the schedule: " + e.getMessage());
        }
        return "redirect:/therapists/" + schedule.getSkinTherapist().getId() + "/schedules";
    }

    @GetMapping("/schedules/edit/{id}")
    public String showEditScheduleForm(@PathVariable UUID id, Model model) {
        TherapistSchedule schedule = therapistScheduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Schedule not found!"));
        model.addAttribute("schedule", schedule);
        return "admin/therapists-schedules/therapists-schedules_edit";
    }

    @PostMapping("/schedules/update/{id}")
    public String updateSchedule(@PathVariable UUID id, @ModelAttribute TherapistSchedule schedule, RedirectAttributes redirectAttributes) {
        try {
            therapistService.updateSchedule(id, schedule);
            redirectAttributes.addFlashAttribute("success", "Schedule has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the schedule: " + e.getMessage());
        }
        return "redirect:/therapists/" + schedule.getSkinTherapist().getId() + "/schedules";
    }

    @GetMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        TherapistSchedule schedule = therapistScheduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Schedule not found!"));
        UUID therapistId = schedule.getSkinTherapist().getId();

        try {
        therapistService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("success", "Schedule has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the schedule: " + e.getMessage());
        }
        return "redirect:/therapists/" + therapistId + "/schedules";
    }

    // Thêm vào TherapistController hiện có
    @GetMapping("/{therapistId}/appointments")
    public String listTherapistAppointments(@PathVariable UUID therapistId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("appointments", appointmentService.getAppointmentsByTherapist(therapistId));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while retrieving appointments: " + e.getMessage());
            return "redirect:/therapists";
        }
        return "user/customer/appointments_list";
    }

    @GetMapping("/appointments/record/{id}")
    public String showRecordResultForm(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.ASSIGNED)
                .stream().filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        model.addAttribute("appointment", appointment);
        return "admin/therapists/therapists_record_result";
    }

    @PostMapping("/appointments/record/{id}")
    public String recordResult(@PathVariable Long id, @RequestParam String result) {
        appointmentService.recordResult(id, result);
        return "redirect:/therapists/" + appointmentService.getAppointmentsByStatus(Appointment.AppointmentStatus.COMPLETED)
            .stream().filter(b -> b.getId().equals(id))
            .findFirst()
            .map(b -> b.getSkinTherapist().getId())
            .orElseThrow(() -> new RuntimeException("Therapist not found!")) + "/appointments";
    }
}
