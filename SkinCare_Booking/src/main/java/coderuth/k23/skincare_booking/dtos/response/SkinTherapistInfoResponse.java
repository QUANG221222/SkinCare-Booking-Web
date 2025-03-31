package coderuth.k23.skincare_booking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SkinTherapistInfoResponse {
    private UUID id;
    private String username;
    private String email;
    private String phone;
}
