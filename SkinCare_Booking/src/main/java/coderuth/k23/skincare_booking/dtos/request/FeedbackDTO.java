package coderuth.k23.skincare_booking.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FeedbackDTO {
    private String username;
    private String email;
    private String subject; // subject là tên vấn đề khách hàng gặp phải
    private String message;

}
