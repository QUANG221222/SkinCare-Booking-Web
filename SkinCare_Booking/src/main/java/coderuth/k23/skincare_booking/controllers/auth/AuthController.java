package coderuth.k23.skincare_booking.controllers.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/register")
    public String RegisterUser() {
        return "auth/register";
    }

    @GetMapping("/registration")
    public String RegisterCustomer(Model model) {

        model.addAttribute("registrationSuccess", "You can check your email to complete your registration");
        return "auth/registrationSuccessful";
    }

    @GetMapping("/login")
    public String LoginUser() {
        return "auth/login";
    }

}
