package coderuth.k23.skincare_booking.mailing;

public interface EmailService {

    void sendMail(final AbstractEmailContext email) throws jakarta.mail.MessagingException;

}
