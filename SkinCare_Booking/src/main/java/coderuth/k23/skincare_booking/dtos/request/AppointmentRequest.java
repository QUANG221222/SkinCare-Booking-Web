package coderuth.k23.skincare_booking.dtos.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Getter
@Setter
public class AppointmentRequest {
    private UUID customerId;
    private UUID skinTherapistId;
    private Long spaServiceId;
    private LocalDateTime appointmentTime;
    private String status;
}