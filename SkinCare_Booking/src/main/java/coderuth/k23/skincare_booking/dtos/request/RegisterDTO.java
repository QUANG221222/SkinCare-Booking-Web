package coderuth.k23.skincare_booking.dtos.request;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String email;
    private String phone;
    private String password;
}
