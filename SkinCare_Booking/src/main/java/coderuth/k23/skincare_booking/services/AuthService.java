package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.RefreshToken;
import coderuth.k23.skincare_booking.repositories.UserBaseRepository;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import coderuth.k23.skincare_booking.jwt.JwtUtil;
import coderuth.k23.skincare_booking.dtos.request.LoginRequest;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.dtos.request.RegisterRequest;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.dtos.response.UserInfoResponse;

import java.util.UUID;

@Service
public class AuthService {
   @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public UserInfoResponse authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(String.valueOf(userDetails.getId()));

        // Add the tokens as HTTP-only cookies
        jwtUtil.addAccessTokenCookie(response, accessToken);
        jwtUtil.addRefreshTokenCookie(response, refreshToken.getToken());

        return new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getUserType(),
                userDetails.getAuthorities().stream().findFirst().get().getAuthority());
    }

    public void registerCustomer(RegisterRequest registerRequest) {
        if(customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (customerRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Create new user account
        Customer customer = new Customer();
        customer.setEmail(registerRequest.getEmail());
        customer.setUsername(registerRequest.getUsername());
        customer.setPhone(registerRequest.getPhone());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setRole(Customer.Role.ROLE_CUSTOMER);//Default role

        customerRepository.save(customer);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getTokenFromCookies(request, "refreshToken");

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        // Validate refresh token and extract user ID
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.verifyExpiration(token);

        // Mark current refresh token as used
        refreshTokenService.markTokenAsUsed(refreshToken);

        // Generate new access token
        Customer customer = (Customer) customerRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailsImpl userDetails = UserDetailsImpl.build(customer);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtUtil.generateAccessToken(authentication);

        // Add the new access token as HTTP-only cookie
        jwtUtil.addAccessTokenCookie(response, newAccessToken);

        // Generate new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userId);

        // Add the new refresh token as HTTP-only cookie
        jwtUtil.addRefreshTokenCookie(response, newRefreshToken.getToken());
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getTokenFromCookies(request, "refreshToken");

        if (refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        // Clear the cookies
        jwtUtil.clearTokenCookies(response);
    }
}
