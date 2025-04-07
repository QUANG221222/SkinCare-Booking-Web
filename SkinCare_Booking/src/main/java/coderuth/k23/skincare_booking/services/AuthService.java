package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.ChangePasswordRequest;
import coderuth.k23.skincare_booking.exception.InvalidTokenException;
import coderuth.k23.skincare_booking.mailing.AccountVerificationEmailContext;
import coderuth.k23.skincare_booking.mailing.EmailService;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.*;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import coderuth.k23.skincare_booking.jwt.JwtUtil;
import coderuth.k23.skincare_booking.dtos.request.LoginRequest;
import coderuth.k23.skincare_booking.dtos.request.RegisterRequest;
import coderuth.k23.skincare_booking.dtos.response.UserInfoResponse;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Value("${site.base.url.https}")
    private String baseUrl;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmailService emailService;

   @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

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
        if (customerRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if(customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (customerRepository.existsByPhone(registerRequest.getPhone())) {
            throw new IllegalArgumentException("Phone is already in use");
        }

        // Create new user account
        Customer customer = new Customer();
        customer.setEmail(registerRequest.getEmail());
        customer.setUsername(registerRequest.getUsername());
        customer.setPhone(registerRequest.getPhone());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setRole(Customer.Role.ROLE_CUSTOMER);//Default role

        customerRepository.save(customer);
        sendRegistrationConfirmationEmail(customer);
    }

    public void sendRegistrationConfirmationEmail(Customer customer) {
        // Đảm bảo customer đã được lưu trong database trước khi tạo token
        if (customer.getId() == null) {
            customer = customerRepository.save(customer);
        }

        SecureToken secureToken = secureTokenService.createToken(customer);
        secureToken.setCustomer(customer);
        secureTokenService.saveSecureToken(secureToken);

        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(customer);
        context.setToken(secureToken.getToken());
        context.buildVerificationUrl(baseUrl, secureToken.getToken());

        try {
            emailService.sendMail(context);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyUser(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if (Objects.isNull(token) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()) {
            throw new InvalidTokenException("Token is not valid");
        }

        Customer customer = customerRepository.getById(secureToken.getCustomer().getId());
        if (Objects.isNull(customer)) {
            return false;
        }

        customer.setAccountVerified(true);
        customerRepository.save(customer);

        secureTokenService.removeToken(secureToken);
        return true;
    }

    public void registerManager(RegisterRequest registerRequest) {
        if (managerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (managerRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        Manager manager = new Manager();
        manager.setEmail(registerRequest.getEmail());
        manager.setUsername(registerRequest.getUsername());
        manager.setPhone(registerRequest.getPhone());
        manager.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        manager.setRole(Manager.Role.ROLE_MANAGER); // ADMIN

        managerRepository.save(manager);
    }

    public void registerStaff(RegisterRequest registerRequest) {
        if (staffRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (staffRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        Staff staff = new Staff();
        staff.setEmail(registerRequest.getEmail());
        staff.setUsername(registerRequest.getUsername());
        staff.setPhone(registerRequest.getPhone());
        staff.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        staff.setRole(Staff.Role.ROLE_STAFF); // STAFF

        staffRepository.save(staff);
    }

    public void registerSkinTherapist(RegisterRequest registerRequest) {
        if (skinTherapistRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (skinTherapistRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        SkinTherapist skinTherapist = new SkinTherapist();
        skinTherapist.setEmail(registerRequest.getEmail());
        skinTherapist.setUsername(registerRequest.getUsername());
        skinTherapist.setPhone(registerRequest.getPhone());
        skinTherapist.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        skinTherapist.setRole(SkinTherapist.Role.ROLE_THERAPIST); // SKIN THERAPIST

        skinTherapistRepository.save(skinTherapist);
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


    public void changePassword(String username, ChangePasswordRequest request) {
        // Find the user by username from all repositories
        User user = findUserByUsername(username);

        // Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Optional: Ensure new password is different from old password
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from the old password");
        }

        // Optional: Add additional password validation (e.g., length, special characters)
        validateNewPassword(request.getNewPassword());

        // Encode and set the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Save the user to the appropriate repository based on user type
        saveUser(user);
    }

    private User findUserByUsername(String username) {
        // Check Customer repository
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isPresent()) {
            return customerOpt.get();
        }

        // Check Manager repository
        Optional<Manager> managerOpt = managerRepository.findByUsername(username);
        if (managerOpt.isPresent()) {
            return managerOpt.get();
        }

        // Check Staff repository
        Optional<Staff> staffOpt = staffRepository.findByUsername(username);
        if (staffOpt.isPresent()) {
            return staffOpt.get();
        }

        // Check SkinTherapist repository
        Optional<SkinTherapist> therapistOpt = skinTherapistRepository.findByUsername(username);
        if (therapistOpt.isPresent()) {
            return therapistOpt.get();
        }

        // If user is not found in any repository, throw an exception
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private void saveUser(User user) {
        if (user instanceof Customer) {
            customerRepository.save((Customer) user);
        } else if (user instanceof Manager) {
            managerRepository.save((Manager) user);
        } else if (user instanceof Staff) {
            staffRepository.save((Staff) user);
        } else if (user instanceof SkinTherapist) {
            skinTherapistRepository.save((SkinTherapist) user);
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
    }

    private void validateNewPassword(String newPassword) {
        // Example: Enforce minimum length and at least one special character
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }
        if (!newPassword.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("New password must contain at least one special character");
        }
    }
}


