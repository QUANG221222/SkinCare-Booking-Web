package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.security.*;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedBackRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SpaServiceRepository spaServiceRepository;
    @Autowired
    private ManagerRepository managerRepository;

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

    public void updateFeedback(Long id, FeedbackRequest feedbackRequest) {
        // Tìm Feedback theo id
        Feedback feedback = feedBackRepository.findByIdNotHidden(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        // Tìm Customer dựa trên username và email
        Customer customer = customerRepository.findByUsernameAndEmail(feedbackRequest.getUsername(), feedbackRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Cập nhật các trường của Feedback
        feedback.setSubject(feedbackRequest.getSubject());
        feedback.setComment(feedbackRequest.getMessage());
        feedback.setCustomer(customer);

        // Lưu Feedback đã cập nhật
        feedBackRepository.save(feedback);

    }

//    // Delete Feedback
//    public void deleteFeedback(Long id, FeedbackRequest feedbackRequest) {
//        // Tìm Feedback theo id
//        Feedback feedback = feedBackRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));
//
//        // Tìm Customer dựa trên username và email
//        Customer customer = customerRepository.findByUsernameAndEmail(feedbackRequest.getUsername(), feedbackRequest.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
//
//        // Kiểm tra xem Feedback có thuộc về Customer này không
//        if (!feedback.getCustomer().getId().equals(customer.getId())) {
//            throw new IllegalArgumentException("You are not authorized to delete this feedback");
//        }
//
//        // Xóa Feedback
//        feedBackRepository.delete(feedback);
//    }

    // Delete Feedback (Soft Delete - Hide Feedback)
    public void deleteFeedback(Long id, FeedbackRequest feedbackRequest) {
        // Tìm Feedback theo id
        Feedback feedback = feedBackRepository.findByIdNotHidden(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        // Kiểm tra xem người dùng có phải là Manager không
        Optional<Manager> managerOptional = managerRepository.findByUsernameAndEmail(
                feedbackRequest.getUsername(), feedbackRequest.getEmail());

        if (managerOptional.isPresent()) {
            // Nếu là Manager, có quyền ẩn bất kỳ Feedback nào
            feedback.setHidden(true);
            feedBackRepository.save(feedback);
            return;
        }

        // Nếu không phải Manager, kiểm tra xem có phải là Customer sở hữu Feedback không
        Customer customer = customerRepository.findByUsernameAndEmail(
                        feedbackRequest.getUsername(), feedbackRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!feedback.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("You are not authorized to hide this feedback");
        }

        // Đánh dấu Feedback là đã ẩn
        feedback.setHidden(true);
        feedBackRepository.save(feedback);
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
