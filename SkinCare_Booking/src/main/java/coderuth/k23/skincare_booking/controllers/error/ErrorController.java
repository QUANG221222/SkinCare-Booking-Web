package coderuth.k23.skincare_booking.controllers.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "access-denied/access-denied"; // Trả về view thông báo
    }
}
