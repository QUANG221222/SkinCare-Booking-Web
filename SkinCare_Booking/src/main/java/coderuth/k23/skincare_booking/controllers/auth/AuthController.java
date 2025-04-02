package coderuth.k23.skincare_booking.controllers.auth;

import coderuth.k23.skincare_booking.dtos.request.RegisterRequest;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.AttributedString;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;
//    //Define register user name
//    @PostMapping("auth/register-customer")
//    public String postMethodName(@RequestBody String entity) {
//
//        return entity;
//    }
    @GetMapping("/register")
    public String RegisterUser() {
        return "auth/register";
    }

    @GetMapping("/registration")
    public String RegisterCustomer(Model model) {

        model.addAttribute("registrationSuccess", "You can check your email to complete your registration");
        return "auth/registrationSuccessful";
    }
//    @GetMapping("/register")
//    public String RegisterUser(RegisterRequest registerRequest, Model model) {
//        authService.registerCustomer(registerRequest);
//        model.addAttribute("registrationSuccess", "You can check your email to complete your registration");
//        return "auth/registrationSuccessful";
//    }

    @GetMapping("/login")
    public String LoginUser() {
        return "auth/login";
    }
}
