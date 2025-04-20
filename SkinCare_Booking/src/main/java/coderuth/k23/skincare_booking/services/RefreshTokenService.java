package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.exception.TokenRefreshException;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.*;
import coderuth.k23.skincare_booking.jwt.JwtUtil;
import coderuth.k23.skincare_booking.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refreshTokenExpirationMs}")
    private int refreshTokenExpirationMs;

    @Value("${app.jwt.maxRefreshTokensPerUser:5}")
    private int maxRefreshTokensPerUser;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public List<RefreshToken> findAllByUserId(String userId) {
        return refreshTokenRepository.findAllByUserId(userId);
    }

    public RefreshToken createRefreshToken(String userId) {
        // Create new refresh token
        String jwtToken = jwtUtil.generateRefreshToken(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(jwtToken);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshToken.setUsed(false);

        // Check number of active tokens for this user
        List<RefreshToken> userTokens = findAllByUserId(userId);

        // If exceeding max allowed tokens, remove oldest
        if (userTokens.size() >= maxRefreshTokensPerUser) {
            // Sort tokens by expiry date and remove oldest one(s)
            userTokens.stream()
                    .sorted((t1, t2) -> t1.getExpiryDate().compareTo(t2.getExpiryDate()))
                    .limit(userTokens.size() - maxRefreshTokensPerUser + 1)
                    .forEach(token -> refreshTokenRepository.delete(token));
        }

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please sign in again.");
        }

        if (token.isUsed()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was already used. This may indicate a replay attack.");
        }

        return token;
    }

    @Transactional
    public void markTokenAsUsed(String token) {
        Optional<RefreshToken> tokenOpt = findByToken(token);
        tokenOpt.ifPresent(refreshToken -> {
            refreshToken.setUsed(true);
            refreshTokenRepository.save(refreshToken);
        });
    }

//    @Transactional
//    public void deleteExpiredTokens() {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("expiryDate").lt(Instant.now()));
//        mongoTemplate.remove(query, RefreshToken.class);
//    }

    @Transactional
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteUsedTokens() {
        refreshTokenRepository.deleteByUsed(true);
    }

    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getTokenFromCookies(request, "refreshToken");

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        // Validate refresh token and extract user ID
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        RefreshToken token = findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        verifyExpiration(token);

        // Mark current refresh token as used
        markTokenAsUsed(refreshToken);

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
        RefreshToken newRefreshToken = createRefreshToken(userId);

        // Add the new refresh token as HTTP-only cookie
        jwtUtil.addRefreshTokenCookie(response, newRefreshToken.getToken());

        return newAccessToken;
    }
}
