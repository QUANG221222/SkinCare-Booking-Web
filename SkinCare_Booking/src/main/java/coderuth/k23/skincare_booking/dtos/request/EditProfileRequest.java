package coderuth.k23.skincare_booking.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditProfileRequest {
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+84|0)[1-9][0-9]{8}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @NotBlank(message = "Location is required")
    @Size(min = 3, max = 100, message = "Location must be between 3 and 100 characters")
    private String location;

}