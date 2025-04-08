package coderuth.k23.skincare_booking.controllers.pages;

import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/feedback")
public class FeedbackWebController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String showFeedbackForm(Model model) {
        // Lấy thông tin người dùng hiện tại
        UserDetails userDetails;
        userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Lấy danh sách Feedback của Customer
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
        model.addAttribute("feedbackRequest", new FeedbackRequest());
        return "user/feedback/feedback";
    }

    @PostMapping
    public String submitFeedback(
            @Valid @ModelAttribute("feedbackRequest") FeedbackRequest feedbackRequest,
            BindingResult result,
            Model model) {
        // Lấy thông tin người dùng hiện tại
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        if (result.hasErrors()) {
            model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
            return "user/feedback/feedback";
        }

        try {
            feedbackService.createFeedback(feedbackRequest);
            model.addAttribute("successMessage", "Feedback submitted successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error submitting feedback: " + e.getMessage());
        }

        model.addAttribute("feedbacks", feedbackService.getFeedbacksByUsername(username, username));
        model.addAttribute("feedbackRequest", new FeedbackRequest());
        return "user/feedback/feedback";
    }
}