package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.jwt.JwtUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.dtos.request.RegisterDTO;
import coderuth.k23.skincare_booking.models.Customer;

import java.util.Collections;

@Service
public class CustomerService implements UserDetailsService {
//    @Autowired
//    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer user = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
        );
    }

//    public UserInfoResponse authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getUsername(),
//                        loginRequest.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//        String accessToken = jwtUtil.generateAccessToken(authentication);
//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
//
//        // Add the tokens as HTTP-only cookies
//        jwtTokenUtil.addAccessTokenCookie(response, accessToken);
//        jwtTokenUtil.addRefreshTokenCookie(response, refreshToken.getToken());
//
//        return new UserInfoResponse(
//                userDetails.getId(),
//                userDetails.getUsername(),
//                userDetails.getEmail(),
//                userDetails.getAuthorities().stream().findFirst().get().getAuthority());
//    }

    public void registerUser(RegisterDTO registerRequest) {
        if(customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (customerRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if(customerRepository.existsByPhone(registerRequest.getPhone())){
            throw new IllegalArgumentException("Phone number is already in use");
        }

        // Create new user account
        Customer customer = new Customer();
        customer.setEmail(registerRequest.getEmail());
        customer.setUsername(registerRequest.getUsername());
        customer.setPhone(registerRequest.getPhone());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        customerRepository.save(customer);
    }
}
