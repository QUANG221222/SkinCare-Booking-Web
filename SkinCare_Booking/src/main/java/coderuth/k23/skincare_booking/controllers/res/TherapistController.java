package coderuth.k23.skincare_booking.controllers.res;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.TherapistSchedule;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;
import coderuth.k23.skincare_booking.services.TherapistService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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

    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

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
        model.addAttribute("therapistId", therapistId);
        model.addAttribute("therapist", therapist);
        return "admin/therapists-schedules/therapists-schedules_create";
    }

    @PostMapping("/schedules")
    public String createSchedule(
            @RequestParam UUID therapistId,
            @RequestParam String dayOfWeek,
            @RequestParam String startTime,
            @RequestParam String endTime,
            RedirectAttributes redirectAttributes) {
        try {
            therapistService.createSchedule(therapistId, dayOfWeek, startTime, endTime);
            redirectAttributes.addFlashAttribute("success", "Schedule has been created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the schedule: " + e.getMessage());
        }
        return "redirect:/therapists/" + therapistId + "/schedules";
    }

    @GetMapping("/schedules/edit/{id}")
    public String showEditScheduleForm(@PathVariable UUID id, Model model) {
        TherapistSchedule schedule = therapistScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found!"));
        String startTime = schedule.getStartTime() != null ? schedule.getStartTime().toString() : "";
        String endTime = schedule.getEndTime() != null ? schedule.getEndTime().toString() : "";
        model.addAttribute("schedule", schedule);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        return "admin/therapists-schedules/therapists-schedules_edit";
    }

    @PostMapping("/schedules/update/{id}")
    public String updateSchedule(
            @PathVariable UUID id,
            @RequestParam String dayOfWeek,
            @RequestParam String startTime,
            @RequestParam String endTime,
            RedirectAttributes redirectAttributes) {
        try {
            TherapistSchedule schedule = therapistService.updateSchedule(id, dayOfWeek, startTime, endTime);
            redirectAttributes.addFlashAttribute("success", "Schedule has been updated successfully!");
            return "redirect:/therapists/" + schedule.getSkinTherapist().getId() + "/schedules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the schedule: " + e.getMessage());
            return "redirect:/therapists";
        }
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

    // Endpoint để lấy lịch làm việc của chuyên viên
    @GetMapping("/{id}/schedules/current")
    @ResponseBody
    public ResponseEntity<List<TherapistSchedule>> getTherapistSchedules(@PathVariable UUID id) {
        try {
            List<TherapistSchedule> schedules = therapistService.getSchedulesByTherapist(id);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Endpoint để tạo lịch làm việc mới
    @PostMapping("/{id}/schedules")
    @ResponseBody
    public ResponseEntity<String> createTherapistSchedule(
            @PathVariable UUID id,
            @RequestBody TherapistSchedule schedule) {
        try {
            therapistService.createSchedule(id, schedule.getDayOfWeek(), schedule.getStartTime().toString(), schedule.getEndTime().toString());
            return ResponseEntity.ok("Schedule created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }
}