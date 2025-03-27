package coderuth.k23.skincare_booking.dtos.response;

import lombok.*;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    boolean authenticated;
}
