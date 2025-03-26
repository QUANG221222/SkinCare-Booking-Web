package coderuth.k23.skincare_booking.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class FeedbackDTO {
    private String username;
    private String email;
    private String subject; // subject là tên vấn đề khách hàng gặp phải
    private String message;
    private Long spaServiceId;

}
