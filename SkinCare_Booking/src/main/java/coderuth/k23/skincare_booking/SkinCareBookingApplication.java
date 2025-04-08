package coderuth.k23.skincare_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkinCareBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkinCareBookingApplication.class, args);
    }

}
