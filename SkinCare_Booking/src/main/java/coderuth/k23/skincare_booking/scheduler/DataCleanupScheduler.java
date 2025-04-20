package coderuth.k23.skincare_booking.scheduler;

import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.RefreshToken;
import coderuth.k23.skincare_booking.models.SecureToken;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.repositories.RefreshTokenRepository;
import coderuth.k23.skincare_booking.repositories.SecureTokenRepository;
import coderuth.k23.skincare_booking.security.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class DataCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private SecureTokenRepository secureTokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedRate = 21600000) // 6h
    @Transactional
    public void deleteUnverifiedCustomers() {
        // Lấy danh sách SecureToken đã hết hạn
        List<SecureToken> expiredTokens = secureTokenRepository.findByExpiredAtBefore(LocalDateTime.now());

        for (SecureToken token : expiredTokens) {
            Customer customer = token.getCustomer();

            // Xóa SecureToken
            secureTokenRepository.delete(token);

            // Nếu Customer chưa xác minh, xóa Customer
            if (!customer.getAccountVerified()) {
                customerRepository.delete(customer);
            }
        }
    }

    @Scheduled(fixedRate = 21600000)
    @Transactional
    public void deleteUsedRefreshTokens() {
        try {
            List<RefreshToken> usedTokens = refreshTokenRepository.findAllByUsed(true);
            if (!usedTokens.isEmpty()) {
                refreshTokenRepository.deleteAll(usedTokens);
                logger.info("Deleted {} used refresh tokens", usedTokens.size());
            } else {
                logger.debug("No used refresh tokens found to delete");
            }
        } catch (Exception e) {
            logger.error("Error while deleting used refresh tokens: {}", e.getMessage(), e);
        }
    }

    @Scheduled(fixedRate = 21600000)
    @Transactional
    public void deleteExpiredRefreshTokens() {
        try {
            List<RefreshToken> expiredTokens = refreshTokenRepository.findByExpiryDateBefore(Instant.now());
            if (!expiredTokens.isEmpty()) {
                refreshTokenRepository.deleteAll(expiredTokens);
                logger.info("Deleted {} Expiry Date refresh tokens", expiredTokens.size());
            } else {
                logger.debug("No Expiry Date refresh tokens found to delete");
            }
        } catch (Exception e) {
            logger.error("Error while deleting expired refresh tokens: {}", e.getMessage(), e);
        }
    }
}