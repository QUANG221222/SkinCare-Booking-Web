package coderuth.k23.skincare_booking.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FeedbackRequest {

    @NotBlank(message = "Subject is required")
    private String subject; // Chủ đề của phản hồi

    @NotBlank(message = "Message is required")
    private String message; // Nội dung phản hồi

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private byte rating; // Điểm đánh giá (1-5)
}
