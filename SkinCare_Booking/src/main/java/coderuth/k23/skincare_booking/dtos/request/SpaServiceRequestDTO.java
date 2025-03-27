package coderuth.k23.skincare_booking.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SpaServiceRequestDTO {
    @NotBlank(message = "Service name is required")
    @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private double price;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours (1440 minutes)")
    private int duration;
}
