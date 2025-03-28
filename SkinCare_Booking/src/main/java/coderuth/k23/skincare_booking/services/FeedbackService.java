package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedBackRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SpaServiceRepository spaServiceRepository;

    // Create Feedback
    public void createFeedback(FeedbackRequest feedbackRequest) {

        Customer customer = customerRepository.findByUsernameAndEmail(feedbackRequest.getUsername(), feedbackRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));


        Feedback feedback = new Feedback();
        feedback.setSubject(feedbackRequest.getSubject());
        feedback.setComment(feedbackRequest.getMessage());
        feedback.setCustomer(customer);

        // Lưu Feedback
        feedBackRepository.save(feedback);
    }

    // Read Feedbacks by Username
    public List<FeedbackRequest> getFeedbacksByUsername(String username) {
        // Tìm Customer dựa trên username
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with username: " + username));

        // Lấy danh sách Feedback của Customer
        List<Feedback> feedbacks = feedBackRepository.findByCustomer(customer);

        // Chuyển đổi từ List<Feedback> sang List<FeedbackDTO>
        return feedbacks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Trả về tất cả feedback
    public List<FeedbackRequest> getAllFeedbacks() {
        return feedBackRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi từ Feedback sang FeedbackDTO
    private FeedbackRequest convertToDTO(Feedback feedback) {
        FeedbackRequest dto = new FeedbackRequest();
        dto.setUsername(feedback.getCustomer().getUsername());
        dto.setEmail(feedback.getCustomer().getEmail());
        dto.setSubject(feedback.getSubject());
        dto.setMessage(feedback.getComment());
        return dto;
    }
}
