package coderuth.k23.skincare_booking.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FeedbackRequest {

    @NotBlank(message = "Subject is required")
    @Size(min = 3, max = 100, message = "subject must be between 3 and 100 . characters")
    private String subject; // Chủ đề của phản hồi

    @NotBlank(message = "Message is required")
    @Size(min = 3, max = 500, message = "message must be between 3 and 500 characters")
    private String message; // Nội dung phản hồi

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private byte rating; // Điểm đánh giá (1-5)

    private Long id;
    private String username; // Thêm trường username
    private boolean isHidden;
    private LocalDateTime createdAt;
}
    // Thêm các trường để hiển thị trong giao diện Manager
//    private String username;

//    private boolean isHidden;

