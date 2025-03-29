package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        feedbackService.createFeedback(feedbackRequest);
        return ResponseEntity.ok(ApiResponse.success("Feedback successfully"));

    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedbackRequest>>> getFeedbacksByUsername(
            @RequestParam(required = false) String username) {
        List<FeedbackRequest> feedbacks;
        if (username != null && !username.isEmpty()) {
            feedbacks = feedbackService.getFeedbacksByUsername(username);
        } else {
            // Nếu không có username, lấy tất cả Feedback
            feedbacks = feedbackService.getAllFeedbacks();
        }
        return ResponseEntity.ok(ApiResponse.success("Feedbacks retrieved successfully", feedbacks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest feedbackRequest) {
        feedbackService.updateFeedback(id, feedbackRequest);
        return ResponseEntity.ok(ApiResponse.success("Feedback updated successfully"));
    }

}
