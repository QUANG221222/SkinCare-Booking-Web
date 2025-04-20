package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.response.BlogResponseDTO;
import coderuth.k23.skincare_booking.services.BlogService;
import coderuth.k23.skincare_booking.models.SpaService;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import coderuth.k23.skincare_booking.services.CenterScheduleService;
import coderuth.k23.skincare_booking.services.TherapistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller

public class PublicPageController {

    @Autowired
    TherapistService therapistService;

    @Autowired
    BlogService blogService;
  
   @Autowired
    SpaServiceRepository spaServiceRepository;

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
    public String userServicesPage(Model model) {
        // Lấy tất cả dịch vụ từ DB
        List<SpaService> services = spaServiceRepository.findAll();

        model.addAttribute("services", services);
        return "user/services";
    }

    @GetMapping("/contact")
    public String userContactPage() {
        return "user/contact";
    }

    @GetMapping("/blog")
    public String userBlogPage(Model model) {
        List<BlogResponseDTO> blogs = blogService.getAllBlogs();
        model.addAttribute("blogs", blogs);
        return "user/customer/blog";
    }

    @GetMapping("/skin-therapist")
    public String userSkinTherapistPage(Model model) {
        model.addAttribute("therapist", therapistService.getAllTherapists());
        return "user/skin-therapist";
    }

    //hiển thị lịch làm việc của trung tâm
    @Autowired
    private CenterScheduleService centerScheduleService;

    @ModelAttribute
    public void addCenterSchedules(Model model) {
        model.addAttribute("centerSchedules", centerScheduleService.getAllSchedules());
    }
}
