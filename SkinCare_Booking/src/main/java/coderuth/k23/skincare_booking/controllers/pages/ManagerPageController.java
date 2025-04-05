package coderuth.k23.skincare_booking.controllers.pages;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerPageController {
    @GetMapping("/protected/manager/home")
    @PreAuthorize("hasRole('MANAGER')")
    public String adminPage() {
        return "admin/index"; // "user/index.html"
    }
}
