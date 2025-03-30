package coderuth.k23.skincare_booking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private UUID id;
    private String username;
    private String email;
//    private String token; // token is restored in HTTPOnly cookie, to prevent "Cross-site scripting (XSS)"
    private String userType;
    private String role;
}
