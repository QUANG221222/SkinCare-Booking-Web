package coderuth.k23.skincare_booking.controllers.authentications;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import coderuth.k23.skincare_booking.services.AuthService;
import coderuth.k23.skincare_booking.dtos.request.LoginRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterRequest;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.dtos.response.UserInfoResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthsController {

    @Autowired
    private AuthService authService;



    @PostMapping("/manager/login")
    public ResponseEntity<ApiResponse<UserInfoResponse>> authenticaAdmin(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserInfoResponse userInfo = authService.authenticateManager(loginRequest, response);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Admin authentication successful", userInfo));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserInfoResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserInfoResponse userInfo = authService.authenticateCustomer(loginRequest, response);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Authentication successful", userInfo));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerCustomer(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerCustomer(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Register successfully"));
    }

    @PostMapping("/manager/register")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerManager(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Admin registered successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Token refreshed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutUser(request, response);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("You've been signed out!"));
    }

}
