package coderuth.k23.skincare_booking.controllers.res;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import coderuth.k23.skincare_booking.services.SpaServiceService;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.dtos.request.SpaServiceRequestDTO;
import coderuth.k23.skincare_booking.dtos.response.SpaServiceResponseDTO;
import coderuth.k23.skincare_booking.models.CenterSchedule;
import coderuth.k23.skincare_booking.models.SpaService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/spa-services")
public class SpaServiceController {

    @Autowired
    private SpaServiceService spaServiceService;

    // Quản lý dịch vụ
    @GetMapping
    public String listServices(Model model) {
        model.addAttribute("SpaService", spaServiceService.getAllServices());
        return "admin/SpaService/service_list";
    }

    @GetMapping("/new")
    public String showCreateServiceForm(Model model) {
        model.addAttribute("SpaService", new SpaService());
        return "admin/SpaService/service_create";
    }

    @PostMapping
    public String createService(@ModelAttribute SpaService spaService) {
        spaServiceService.createService(spaService);
        return "redirect:/spa-services";
    }

    @GetMapping("/edit/{id}")
    public String showEditServiceForm(@PathVariable Long id, Model model) {
        SpaService spaService = spaServiceService.getAllServices().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ!"));
        model.addAttribute("SpaService", spaService);
        return "admin/SpaService/service_edit";
    }

    @PostMapping("/update/{id}")
    public String updateService(@PathVariable Long id, @ModelAttribute SpaService spaService) {
        spaServiceService.updateService(id, spaService);
        return "redirect:/spa-services";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id,  RedirectAttributes redirectAttributes) {
        // Kiểm tra xem dịch vụ có đang được sử dụng trong lịch hẹn không
        Optional<SpaService> spaServiceOptional = spaServiceService.getAllServices().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();

        if (spaServiceOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ để xóa.");
            return "redirect:/spa-services";
        }

        try {
            spaServiceService.deleteService(id, redirectAttributes);
            redirectAttributes.addFlashAttribute("success", "Xóa dịch vụ thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa dịch vụ vì đang được sử dụng trong các cuộc hẹn.");
        }
        return "redirect:/spa-services";
    }
    // Quản lý lịch làm việc trung tâm
    @GetMapping("/schedules")
    public String listSchedules(Model model) {
        model.addAttribute("schedules", spaServiceService.getAllSchedules());
        return "/admin/Schedules/centerSchedule_List";
    }

    @GetMapping("/schedules/new")
    public String showCreateScheduleForm(Model model) {
        model.addAttribute("schedules", new CenterSchedule());
        return "/admin/Schedules/centerSchedule_Create";
    }

    @PostMapping("/schedules")
    public String createSchedule(@ModelAttribute CenterSchedule schedule, RedirectAttributes redirectAttributes) {
        spaServiceService.createSchedule(schedule);
        redirectAttributes.addFlashAttribute("success", "Tạo lịch làm việc thành công!");
        return "redirect:/spa-services/schedules";
    }

    @GetMapping("/schedules/edit/{id}")
    public String showEditScheduleForm(@PathVariable Long id, Model model) {
        CenterSchedule schedule = spaServiceService.getAllSchedules().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc!"));
        model.addAttribute("schedules", schedule);
        return "/admin/Schedules/centerSchedule_Edit";
    }

    @PostMapping("/schedules/update/{id}")
    public String updateSchedule(@PathVariable Long id, @ModelAttribute CenterSchedule schedule, RedirectAttributes redirectAttributes) {
        spaServiceService.updateSchedule(id, schedule);
        redirectAttributes.addFlashAttribute("success", "Sửa lịch làm việc thành công!");
        return "redirect:/spa-services/schedules";
    }

    @GetMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        spaServiceService.deleteSchedule(id);
        redirectAttributes.addFlashAttribute("success", "Xóa lịch làm việc thành công!");
        return "redirect:/spa-services/schedules";
    }
}
