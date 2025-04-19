package coderuth.k23.skincare_booking.dtos.request;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SpaServiceRequestDTO {
    @NotBlank(message = "Service name is required")
    @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Image URL is required")
    @Pattern(
            regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|png|webp)$",
            message = "Image must be a valid URL ending with .jpg, .jpeg, or .png"
    )
    private String imageUrl;

     @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours (1440 minutes)")
    private int duration;
}
