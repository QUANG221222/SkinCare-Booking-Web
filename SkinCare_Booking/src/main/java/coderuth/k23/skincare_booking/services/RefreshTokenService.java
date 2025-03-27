package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.exception.TokenRefreshException;
import coderuth.k23.skincare_booking.models.RefreshToken;
import coderuth.k23.skincare_booking.repositories.RefreshTokenRepository;
import coderuth.k23.skincare_booking.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
}
