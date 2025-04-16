package coderuth.k23.skincare_booking.controllers.res;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import coderuth.k23.skincare_booking.services.SpaServiceService;
import coderuth.k23.skincare_booking.models.CenterSchedule;
import coderuth.k23.skincare_booking.models.SpaService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/spa-services")
public class SpaServiceController {

    @Autowired
    private SpaServiceService spaServiceService;

    @GetMapping("/services")
    public String showAllServices(Model model) {
        List<SpaService> services = spaServiceService.getAllServices(); // Lấy từ DB
        model.addAttribute("services", services); // Gửi qua view
        return "user/master/Services/service"; // Tên file HTML
    }

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
//
//    @PostMapping
//    public String createService(@ModelAttribute SpaService spaService, RedirectAttributes redirectAttributes) {
//        try {
//            spaServiceService.createService(spaService);
//            redirectAttributes.addFlashAttribute("success", "Service has been created successfully!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the service: " + e.getMessage());
//        }
//        return "redirect:/spa-services";
//    }

    @GetMapping("/edit/{id}")
    public String showEditServiceForm(@PathVariable Long id, Model model) {
        SpaService spaService = spaServiceService.getAllServices().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Service not found!"));
        model.addAttribute("SpaService", spaService);
        return "admin/SpaService/service_edit";
    }

    @PostMapping("/update/{id}")
    public String updateService(@PathVariable Long id, @ModelAttribute SpaService spaService, RedirectAttributes redirectAttributes) {
        try {
            spaServiceService.updateService(id, spaService);
            redirectAttributes.addFlashAttribute("success", "Service has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the service: " + e.getMessage());
        }
        return "redirect:/spa-services";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id,  RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra xem dịch vụ có đang được sử dụng trong lịch hẹn không
            Optional<SpaService> spaServiceOptional = spaServiceService.getAllServices().stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst();
    
            if (spaServiceOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Service not found to delete.");
                return "redirect:/spa-services";
            }
    
            spaServiceService.deleteService(id, redirectAttributes);
            redirectAttributes.addFlashAttribute("success", "Service has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the service: " + e.getMessage());
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
        try {
            spaServiceService.createSchedule(schedule);
            redirectAttributes.addFlashAttribute("success", "Schedule has been created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the schedule: " + e.getMessage());
        }
        return "redirect:/spa-services/schedules";
    }

    @GetMapping("/schedules/edit/{id}")
    public String showEditScheduleForm(@PathVariable Long id, Model model) {
        CenterSchedule schedule = spaServiceService.getAllSchedules().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Schedule not found!"));
        model.addAttribute("schedules", schedule);
        return "/admin/Schedules/centerSchedule_Edit";
    }

    public String updateSchedule(@PathVariable Long id, @ModelAttribute CenterSchedule schedule, RedirectAttributes redirectAttributes) {
        try {
            spaServiceService.updateSchedule(id, schedule);
            redirectAttributes.addFlashAttribute("success", "Schedule has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the schedule: " + e.getMessage());
        }
        return "redirect:/spa-services/schedules";
    }

    @GetMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            spaServiceService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("success", "Schedule has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the schedule: " + e.getMessage());
        }
        return "redirect:/spa-services/schedules";
    }
}
