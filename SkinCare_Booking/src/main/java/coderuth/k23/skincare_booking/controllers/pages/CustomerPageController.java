package coderuth.k23.skincare_booking.controllers.pages;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

@Controller
//@GetMapping("/protected/user")
public class CustomerPageController {
    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }


    // Guest
    @GetMapping("/protected/customer/")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String userHomePage() {
        return "user/index"; // Load file src/main/resources/templates/user/index.html
    }

    @GetMapping("/protected/customer/about-us")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String userAboutUsPage() {
        return "user/about-us";
    }

    @GetMapping("/protected/customer/services")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String userServicesPage() {
        return "user/services";
    }

    @GetMapping("/protected/customer/contact")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String userContactPage() {
        return "user/contact";
    }

    @GetMapping("/protected/customer/blog")
    @PreAuthorize("hasRole('CUSTOMER')")
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

