package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.SecureToken;
import coderuth.k23.skincare_booking.repositories.SecureTokenRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SecureTokenService {

    private static BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(12);

    @Value("2800") // 46m = 2800s
    private int tokenValidityInSeconds;

    @Autowired
    private SecureTokenRepository secureTokenRepository;


    public SecureToken createToken(Customer customer) {
        String tokenValue = new String(Base64.encodeBase64URLSafeString(DEFAULT_TOKEN_GENERATOR.generateKey()));
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(tokenValue);
        secureToken.setExpiredAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds));
        secureToken.setCustomer(customer);
        this.saveSecureToken(secureToken);
        return secureToken;
    }


    public void saveSecureToken(SecureToken secureToken) {
        secureTokenRepository.save(secureToken);
    }


    public SecureToken findByToken(String token) {
        return secureTokenRepository.findByToken(token);
    }

    public void removeToken(SecureToken token) {
        secureTokenRepository.delete(token);
    }
}
