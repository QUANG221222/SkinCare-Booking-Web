package coderuth.k23.skincare_booking.dtos.request;
import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
