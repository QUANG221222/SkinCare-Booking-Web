package coderuth.k23.skincare_booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserPageController {
    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/")
    public String userHomePage() {
        return "user/index"; // Load file src/main/resources/templates/user/index.html
    }

    @GetMapping("/about-us")
    public String userAboutUsPage() {
        return "user/about-us";
    }

    @GetMapping("/services")
    public String userServicesPage() {
        return "user/services";
    }

    @GetMapping("/contact")
    public String userContactPage() {
        return "user/contact";
    }
    @GetMapping("/blog")
    public String userBlog() { return "user/blog"; }

    @GetMapping("/master")
    public String userMasterPage() {
        return "user/master/masterpage";
    }

    @GetMapping("/rooms")
    public String userElementPage() {
        return "user/rooms";
    }
}

