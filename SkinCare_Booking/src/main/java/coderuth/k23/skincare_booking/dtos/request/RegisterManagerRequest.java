package coderuth.k23.skincare_booking.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterManagerRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    private String fullname;

    @NotBlank(message = "Image URL is required")
    private String img;

    @NotBlank(message = "Email is required")
    @Size(max = 50)
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+84|0)[1-9][0-9]{8}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,40}$",
            message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character. Ex: Abc@123"
    )
    private String password;
}
