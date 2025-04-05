package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

//    // Read Feedbacks by Username
//    public List<FeedbackRequest> getFeedbacksByUsername(String username) {
//        // Tìm Customer dựa trên username
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new IllegalArgumentException("Customer not found with username: " + username));
//
//        // Lấy danh sách Feedback của Customer
//        List<Feedback> feedbacks = feedBackRepository.findByCustomer(customer);
//
//        // Chuyển đổi từ List<Feedback> sang List<FeedbackDTO>
//        return feedbacks.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    // Trả về tất cả feedback
//    public List<FeedbackRequest> getAllFeedbacks() {
//        return feedBackRepository.findAllNotHidden().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

    public List<FeedbackRequest> getFeedbacksByUsername(String username, String requesterUsername, String requesterEmail) {
        // Tìm Customer dựa trên username
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with username: " + username));

        // Kiểm tra xem người yêu cầu có phải là Manager không
        Optional<Manager> managerOptional = managerRepository.findByUsernameAndEmail(requesterUsername, requesterEmail);

        List<Feedback> feedbacks;
        if (managerOptional.isPresent()) {
            // Nếu là Manager, trả về tất cả Feedback (bao gồm cả Feedback đã ẩn)
            feedbacks = feedBackRepository.findByCustomer(customer);
        } else {
            // Nếu không phải Manager, kiểm tra xem có phải là Customer sở hữu Feedback không
            Customer requester = customerRepository.findByUsernameAndEmail(requesterUsername, requesterEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Requester not found"));

            if (!customer.getId().equals(requester.getId())) {
                throw new IllegalArgumentException("You are not authorized to view this customer's feedbacks");
            }

            // Chỉ trả về Feedback chưa bị ẩn
            feedbacks = feedBackRepository.findByCustomerNotHidden(customer);
        }

        // Chuyển đổi từ List<Feedback> sang List<FeedbackRequest>
        return feedbacks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Trả về tất cả feedback
    public List<FeedbackRequest> getAllFeedbacks(String requesterUsername, String requesterEmail) {
        // Kiểm tra xem người yêu cầu có phải là Manager không
        Optional<Manager> managerOptional = managerRepository.findByUsernameAndEmail(requesterUsername, requesterEmail);

        List<Feedback> feedbacks;
        if (managerOptional.isPresent()) {
            // Nếu là Manager, trả về tất cả Feedback (bao gồm cả Feedback đã ẩn)
            feedbacks = feedBackRepository.findAllIncludingHidden();
        } else {
            // Nếu không phải Manager, kiểm tra xem có phải là Customer không
            customerRepository.findByUsernameAndEmail(requesterUsername, requesterEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Requester not found"));

            // Chỉ trả về Feedback chưa bị ẩn
            feedbacks = feedBackRepository.findAllNotHidden();
        }

        return feedbacks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    //update feedback
    public void updateFeedback(Long id, FeedbackRequest feedbackRequest) {
        // Tìm Feedback theo id
        Feedback feedback = feedBackRepository.findByIdNotHidden(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        // Tìm Customer dựa trên username và email
        Customer customer = customerRepository.findByUsernameAndEmail(feedbackRequest.getUsername(), feedbackRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Kiểm tra xem Customer có phải là chủ sở hữu của Feedback không
        if (!feedback.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("You are not authorized to update this feedback");
        }

        // Cập nhật các trường của Feedback
        feedback.setSubject(feedbackRequest.getSubject());
        feedback.setComment(feedbackRequest.getMessage());
        feedback.setCustomer(customer);

        // Lưu Feedback đã cập nhật
        feedBackRepository.save(feedback);
    }

    // Delete Feedback
    public void deleteFeedback(Long id, FeedbackRequest feedbackRequest, boolean isPermanent) {
        // Kiểm tra xem người dùng có phải là Manager không
        Optional<Manager> managerOptional = managerRepository.findByUsernameAndEmail(
                feedbackRequest.getUsername(), feedbackRequest.getEmail());

        Feedback feedback;
        if (managerOptional.isPresent()) {
            // Nếu là Manager, tìm Feedback bằng findById (có thể tìm cả Feedback đã ẩn)
            feedback = feedBackRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

            if (isPermanent) {
                // Hard delete: Xóa hoàn toàn khỏi cơ sở dữ liệu
                feedBackRepository.delete(feedback);
            } else {
                // Soft delete: Đặt isHidden = true
                feedback.setHidden(true);
                feedBackRepository.save(feedback);
            }
            return;
        }

        // Nếu không phải Manager, kiểm tra xem có phải là Customer sở hữu Feedback không
        // Customer chỉ có thể thao tác với Feedback chưa bị ẩn
        feedback = feedBackRepository.findByIdNotHidden(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        Customer customer = customerRepository.findByUsernameAndEmail(
                        feedbackRequest.getUsername(), feedbackRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!feedback.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("You are not authorized to delete this feedback");
        }

        // Customer chỉ có thể soft delete
        if (isPermanent) {
            throw new IllegalArgumentException("Only managers can permanently delete feedback");
        }

        // Soft delete cho Customer
        feedback.setHidden(true);
        feedBackRepository.save(feedback);
    }

    public void unhideFeedback(Long id, FeedbackRequest feedbackRequest) {
        // Tìm Feedback theo id (không cần kiểm tra isHidden vì Manager có thể hiển thị lại bất kỳ Feedback nào)
        Feedback feedback = feedBackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        // Kiểm tra xem người dùng có phải là Manager không
        Optional<Manager> managerOptional = managerRepository.findByUsernameAndEmail(
                feedbackRequest.getUsername(), feedbackRequest.getEmail());

        if (!managerOptional.isPresent()) {
            throw new IllegalArgumentException("Only managers can unhide feedback");
        }

        // Hiển thị lại Feedback
        feedback.setHidden(false);
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
