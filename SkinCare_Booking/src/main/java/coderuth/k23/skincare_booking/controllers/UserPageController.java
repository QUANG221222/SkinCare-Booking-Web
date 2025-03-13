package coderuth.k23.skincare_booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {
    @GetMapping("/user")
    public String userPage() {
        return "user/index"; // Load file src/main/resources/templates/user/index.html
    }
}

