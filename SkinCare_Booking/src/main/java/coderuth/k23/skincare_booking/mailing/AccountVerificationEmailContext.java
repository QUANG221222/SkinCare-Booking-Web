package coderuth.k23.skincare_booking.mailing;

import coderuth.k23.skincare_booking.models.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    private String token;

    @Override
    public <T> void init(T context) {
        Customer customer = (Customer) context;

        put("username", customer.getUsername());
        setTemplateLocation("mailing/email-verification");
        setSubject("Complete Your Registration");
        setFrom("nguyennhatquang.2509@gmail.com");
        setTo(customer.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token) {
        final String url = UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/register/verify").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }

}
