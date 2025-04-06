package coderuth.k23.skincare_booking.controllers.res;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.TherapistSchedule;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;
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
    public String createTherapist(@ModelAttribute SkinTherapist therapist) {
        therapistService.createTherapist(therapist);
        return "redirect:/therapists";
    }

    @GetMapping("/edit/{id}")
    public String showEditTherapistForm(@PathVariable UUID id, Model model) {
        SkinTherapist therapist = therapistService.getAllTherapists().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà trị liệu!"));
        model.addAttribute("therapist", therapist);
        return "admin/therapists/therapists_edit";
    }

    @PostMapping("/update/{id}")
    public String updateTherapist(@PathVariable UUID id, @ModelAttribute SkinTherapist therapist) {
        therapistService.updateTherapist(id, therapist);
        return "redirect:/therapists";
    }

    @GetMapping("/delete/{id}")
    public String deleteTherapist(@PathVariable UUID id) {
        therapistService.deleteTherapist(id);
        return "redirect:/therapists";
    }

    // Quản lý lịch làm việc của nhà trị liệu
    @GetMapping("/{therapistId}/schedules")
    public String listSchedulesByTherapist(@PathVariable UUID therapistId, Model model) {
        SkinTherapist therapist = skinTherapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà trị liệu!"));
        model.addAttribute("therapist", therapist);
        model.addAttribute("schedules", therapistService.getSchedulesByTherapist(therapistId));
        return "admin/therapists-schedules/therapists-schedules_list";
    }

    @GetMapping("/{therapistId}/schedules/new")
    public String showCreateScheduleForm(@PathVariable UUID therapistId, Model model) {
        SkinTherapist therapist = skinTherapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà trị liệu!"));
        TherapistSchedule schedule = new TherapistSchedule();
        schedule.setSkinTherapist(therapist);
        model.addAttribute("schedule", schedule);
        return "admin/therapists-schedules/therapists-schedules_create";
    }

    @PostMapping("/schedules")
    public String createSchedule(@ModelAttribute TherapistSchedule schedule) {
        therapistService.createSchedule(schedule);
        return "redirect:/therapists/" + schedule.getSkinTherapist().getId() + "/schedules";
    }

    @GetMapping("/schedules/edit/{id}")
    public String showEditScheduleForm(@PathVariable UUID id, Model model) {
        TherapistSchedule schedule = therapistScheduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc!"));
        model.addAttribute("schedule", schedule);
        return "admin/therapists-schedules/therapists-schedules_edit";
    }

    @PostMapping("/schedules/update/{id}")
    public String updateSchedule(@PathVariable UUID id, @ModelAttribute TherapistSchedule schedule) {
        therapistService.updateSchedule(id, schedule);
        return "redirect:/therapists/" + schedule.getSkinTherapist().getId() + "/schedules";
    }

    @GetMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable UUID id) {
        TherapistSchedule schedule = therapistScheduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc!"));
        UUID therapistId = schedule.getSkinTherapist().getId();
        therapistService.deleteSchedule(id);
        return "redirect:/therapists/" + therapistId + "/schedules";
    }
}
