package coderuth.k23.skincare_booking.controllers.res;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import coderuth.k23.skincare_booking.jwt.JwtUtil;
import coderuth.k23.skincare_booking.services.CustomerService;
import coderuth.k23.skincare_booking.dtos.request.LoginRequest;
import coderuth.k23.skincare_booking.dtos.response.AuthResponse;
import coderuth.k23.skincare_booking.dtos.request.RegisterDTO;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthsController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerService customerService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = customerService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateAccessToken(userDetails.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
        }
    }
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<UserInfoResponse>> authenticateUser(
//            @Valid @RequestBody LoginRequest loginRequest,
//            HttpServletResponse response) {
//        UserInfoResponse userInfo = authService.authenticateUser(loginRequest, response);
//        return ResponseEntity.ok(ApiResponse.success("Authentication successful", userInfo));
//    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerCustomer(@Valid @RequestBody RegisterDTO registerDTO) {
        customerService.registerUser(registerDTO);
        return ResponseEntity.ok(ApiResponse.success("Register successfully"));
    }

    @GetMapping("/profile")
    public String getUserProfile(HttpServletRequest request) {
        // Lấy token từ header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Missing or invalid token!";
        }

        // Cắt bỏ "Bearer " để lấy token thực sự
        String token = authHeader.substring(7);

        return "Token: " + token; // Test xem có nhận được token không
    }
}
