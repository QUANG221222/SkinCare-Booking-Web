package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        feedbackService.createFeedback(feedbackRequest);
        return ResponseEntity.ok(ApiResponse.success("Feedback successfully"));

    }



    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<FeedbackRequest>>> getFeedbacksByUsername(@RequestParam(required = false) String username) {
        List<FeedbackRequest> feedbacks;
        if (username != null && !username.isEmpty()) {
            feedbacks = feedbackService.getFeedbacksByUsername(username);
        } else {
            // Nếu không có username, lấy tất cả Feedback
            feedbacks = feedbackService.getAllFeedbacks();
        }
        return ResponseEntity.ok(ApiResponse.success("Feedbacks retrieved successfully", feedbacks));
    }

//    @GetMapping
//    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('STAFF')")
//    public ResponseEntity<ApiResponse<List<FeedbackRequest>>> getFeedbacksByUsername(
//            @RequestParam(required = false) String username,
//            @Valid @RequestBody FeedbackRequest requester) {
//        try {
//            List<FeedbackRequest> feedbacks;
//            if (username != null && !username.isEmpty()) {
//                feedbacks = feedbackService.getFeedbacksByUsername(username, requester.getUsername(), requester.getEmail());
//            } else {
//                feedbacks = feedbackService.getAllFeedbacks(requester.getUsername(), requester.getEmail());
//            }
//            return ResponseEntity.ok(ApiResponse.success("Feedbacks retrieved successfully", feedbacks));
//        } catch (IllegalArgumentException e) {
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
//            } else if (e.getMessage().contains("not authorized")) {
//                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
//            }
//            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
//        }
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> updateFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequest feedbackRequest) {
        try {
            feedbackService.updateFeedback(id, feedbackRequest);
            return ResponseEntity.ok(ApiResponse.success("Feedback updated successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            } else if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean permanent,
            @Valid @RequestBody FeedbackRequest feedbackRequest) {
        try {
            feedbackService.deleteFeedback(id, feedbackRequest, permanent);
            String message = permanent ? "Feedback permanently deleted successfully" : "Feedback hidden successfully";
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            } else if (e.getMessage().contains("not authorized") || e.getMessage().contains("Only managers")) {
                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/unhide")
    public ResponseEntity<ApiResponse<Void>> unhideFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequest feedbackRequest) {
        try {
            feedbackService.unhideFeedback(id, feedbackRequest);
            return ResponseEntity.ok(ApiResponse.success("Feedback unhidden successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            } else if (e.getMessage().contains("Only managers")) {
                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }
}



