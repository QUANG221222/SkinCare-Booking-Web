package coderuth.k23.skincare_booking.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

@Controller
//@GetMapping("/protected/user")
public class UserPageController {
    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }


    // Guest
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
    public String userBlogPage() {
        return "user/blog";
    }

//    // Customer: đặt bộ lọc security với yêu cầu phải có jwt token truy cập. spring security filter
//    @GetMapping("/customer/profile")
//    public String customerPage(@PathVariable String id) {
//        return "user/customer/" + id;
//    }
    // @GetMapping("/customer/booking")
}

