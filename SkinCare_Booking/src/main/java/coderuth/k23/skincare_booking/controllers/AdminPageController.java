package coderuth.k23.skincare_booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {
    @GetMapping("/admin")
    public String adminPage() {
        return "admin/index"; // "user/index.html"
    }
}
