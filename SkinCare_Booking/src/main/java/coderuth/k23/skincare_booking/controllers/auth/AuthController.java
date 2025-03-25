package coderuth.k23.skincare_booking.controllers.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {
    //Define register user name
    @PostMapping("auth/register-customer")
    public String postMethodName(@RequestBody String entity) {

        return entity;
    }
//    @GetMapping("auth/register")
//    public String RegisterUser() {
//        return "auth/register";
//    }
//    @GetMapping("auth/login")
//    public String LoginUser() {
//        return "auth/login";
//    }
}
