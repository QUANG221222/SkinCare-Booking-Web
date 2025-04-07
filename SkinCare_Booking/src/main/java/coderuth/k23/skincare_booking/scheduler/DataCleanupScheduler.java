package coderuth.k23.skincare_booking.scheduler;

import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.SecureToken;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.repositories.SecureTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataCleanupScheduler {

    @Autowired
    private SecureTokenRepository secureTokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

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
}