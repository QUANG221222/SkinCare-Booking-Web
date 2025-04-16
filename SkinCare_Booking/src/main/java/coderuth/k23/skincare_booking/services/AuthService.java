package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import coderuth.k23.skincare_booking.jwt.JwtUtil;
import coderuth.k23.skincare_booking.dtos.response.UserInfoResponse;

import java.util.Objects;
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

    public void registerManager(RegisterManagerRequest registerManagerRequest) {
        if (managerRepository.existsByEmail(registerManagerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (managerRepository.existsByUsername(registerManagerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (managerRepository.existsByPhone(registerManagerRequest.getPhone())) {
            throw new IllegalArgumentException("Phone is already in use");
        }

        Manager manager = new Manager();
        manager.setUsername(registerManagerRequest.getUsername());
        manager.setFullName(registerManagerRequest.getFullname());
        manager.setImg(registerManagerRequest.getImg());
        manager.setEmail(registerManagerRequest.getEmail());
        manager.setPhone(registerManagerRequest.getPhone());
        manager.setPassword(passwordEncoder.encode(registerManagerRequest.getPassword()));
        manager.setRole(Manager.Role.ROLE_MANAGER); // ADMIN

        managerRepository.save(manager);
    }

    public void registerStaff(RegisterStaffRequest registerStaffRequest) {
        if (staffRepository.existsByEmail(registerStaffRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (staffRepository.existsByUsername(registerStaffRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (staffRepository.existsByPhone(registerStaffRequest.getPhone())) {
            throw new IllegalArgumentException("Phone is already in use");
        }

        Staff staff = new Staff();
        staff.setUsername(registerStaffRequest.getUsername());
        staff.setFullName(registerStaffRequest.getFullname());
        staff.setImg(registerStaffRequest.getImg());
        staff.setEmail(registerStaffRequest.getEmail());
        staff.setPhone(registerStaffRequest.getPhone());
        staff.setPassword(passwordEncoder.encode(registerStaffRequest.getPassword()));
        staff.setRole(Staff.Role.ROLE_STAFF); // STAFF

        staffRepository.save(staff);
    }

    public void registerSkinTherapist(RegisterTherapistRequest registerRequest) {
        if (skinTherapistRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (skinTherapistRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (skinTherapistRepository.existsByPhone(registerRequest.getPhone())) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        SkinTherapist skinTherapist = new SkinTherapist();
        skinTherapist.setEmail(registerRequest.getEmail());
        skinTherapist.setUsername(registerRequest.getUsername());
        skinTherapist.setFullName(registerRequest.getFullname());
        skinTherapist.setPhone(registerRequest.getPhone());
        skinTherapist.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        skinTherapist.setSpecialty(registerRequest.getSpecialty());
        skinTherapist.setImg(registerRequest.getImg());
        skinTherapist.setRole(SkinTherapist.Role.ROLE_THERAPIST); // THERAPIST

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

        // Determine user type and fetch the corresponding user
        Object user;
        if (customerRepository.findById(UUID.fromString(userId)).isPresent()) {
            user = customerRepository.findById(UUID.fromString(userId)).get();
        } else if (managerRepository.findById(UUID.fromString(userId)).isPresent()) {
            user = managerRepository.findById(UUID.fromString(userId)).get();
        } else if (staffRepository.findById(UUID.fromString(userId)).isPresent()) {
            user = staffRepository.findById(UUID.fromString(userId)).get();
        } else if (skinTherapistRepository.findById(UUID.fromString(userId)).isPresent()) {
            user = skinTherapistRepository.findById(UUID.fromString(userId)).get();
        } else {
            throw new RuntimeException("User not found");
        }

        // Build UserDetails based on the user type
        UserDetailsImpl userDetails;
        if (user instanceof Customer) {
            userDetails = UserDetailsImpl.build((Customer) user);
        } else if (user instanceof Manager) {
            userDetails = UserDetailsImpl.build((Manager) user);
        } else if (user instanceof Staff) {
            userDetails = UserDetailsImpl.build((Staff) user);
        } else if (user instanceof SkinTherapist) {
            userDetails = UserDetailsImpl.build((SkinTherapist) user);
        } else {
            throw new RuntimeException("Unsupported user type");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Generate new access token
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
