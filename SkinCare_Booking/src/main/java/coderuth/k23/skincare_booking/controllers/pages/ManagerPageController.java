package coderuth.k23.skincare_booking.controllers.pages;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected/manager")
public class ManagerPageController {
    @GetMapping("/home")
    @PreAuthorize("hasRole('MANAGER')")
    public String adminPage() {
        return "admin/index"; // "user/index.html"
    }

    @GetMapping("/spa-services")
    @PreAuthorize("hasRole('MANAGER')")
    public String spaServicesPage() {
        return "redirect:/spa-services"; 
    }

    @GetMapping("/spa-services/schedules")
    @PreAuthorize("hasRole('MANAGER')")
    public String centerSchedulesPage() {
        return "redirect:/spa-services/schedules"; 
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/therapists")
    public String TherapistPage() {
        return "redirect:/therapists"; 
    }
    
}
