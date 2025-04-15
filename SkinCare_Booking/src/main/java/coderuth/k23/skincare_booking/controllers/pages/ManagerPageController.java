package coderuth.k23.skincare_booking.controllers.pages;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected/manager")
@PreAuthorize("hasRole('STAFF') or hasRole('MANAGER')")
public class ManagerPageController {
    @GetMapping("/home")
    public String adminPage() {
        return "admin/index"; // "user/index.html"
    }

    @GetMapping("/spa-services")
    public String spaServicesPage() {
        return "redirect:/spa-services"; 
    }

    @GetMapping("/spa-services/schedules")
    public String centerSchedulesPage() {
        return "redirect:/spa-services/schedules"; 
    }


    @GetMapping("/therapists")
    public String TherapistPage() {
        return "redirect:/therapists"; 
    }

    @GetMapping("/appointments/pending")
    public String appointmentsPage() {
        return "redirect:/protected/staff/appointments/pending"; 
    }
    
}
