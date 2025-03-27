package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.request.FeedbackDTO;
import coderuth.k23.skincare_booking.dtos.request.RegisterDTO;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createFeedback(@Valid @RequestBody FeedbackDTO feedbackDTO) {
        feedbackService.createFeedback(feedbackDTO);
        return ResponseEntity.ok(ApiResponse.success("Feedback successfully"));

    }


}
