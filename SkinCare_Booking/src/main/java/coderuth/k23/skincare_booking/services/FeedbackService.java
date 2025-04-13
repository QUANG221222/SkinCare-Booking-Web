package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.FeedbackRequest;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
// Class để lưu thông tin số lượng feedback và thời gian bắt đầu chuỗi
class FeedbackLimitInfo {
    private int count; // Số lượng feedback đã gửi
    private LocalDateTime startTime; // Thời gian bắt đầu chuỗi

    public FeedbackLimitInfo(int count, LocalDateTime startTime) {
        this.count = count;
        this.startTime = startTime;
    }}

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedBackRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // Lưu thời gian gửi feedback gần nhất của từng khách hàng (in-memory)
    private final Map<String, FeedbackLimitInfo> feedbackLimits = new ConcurrentHashMap<>(); //<key: String:username,value: FeedbackLimitInfo: count>

    private static final byte MAX_FEEDBACKS_PER_CYCLE = 3; // Giới hạn 3 feedback
    private static final byte COOLDOWN_MINUTES = 30; // 30 phút

    // Create Feedback
    public void createFeedback(FeedbackRequest feedbackRequest) {

        // Lấy thông tin người dùng từ SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Tìm Customer dựa trên username
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with username: " + username));

        // Kiểm tra thời gian gửi feedback gần nhất
        LocalDateTime now = LocalDateTime.now();
        FeedbackLimitInfo limitInfo = feedbackLimits.get(username);
        if (limitInfo != null) //khách hàng đã gửi feed back
             {
            long minutesSinceStart = ChronoUnit.MINUTES.between(limitInfo.getStartTime(), now);
            if (minutesSinceStart < COOLDOWN_MINUTES) {
                // Chưa đủ 30 phút, kiểm tra số lượng feedback
                if (limitInfo.getCount() >= MAX_FEEDBACKS_PER_CYCLE) {
                    throw new IllegalStateException("You have reached the maximum of 3 feedbacks. Please wait " + (COOLDOWN_MINUTES - minutesSinceStart) + " more minutes to send another feedback.");
                }
            } else {
                // Đã quá 30 phút, reset số lượng và thời gian
                feedbackLimits.remove(username);
                limitInfo = null;
            }
        }

        Feedback feedback = new Feedback();
        feedback.setSubject(feedbackRequest.getSubject());
        feedback.setComment(feedbackRequest.getMessage());
        feedback.setRating(feedbackRequest.getRating());
        feedback.setCustomer(customer);
        //lưu feedback
        feedBackRepository.save(feedback);

        // Cập nhật số lượng feedback và thời gian
        if (limitInfo == null) {
            // Bắt đầu chuỗi mới
            limitInfo = new FeedbackLimitInfo(1, now);
        } else {
            // Tăng số lượng feedback trong chuỗi hiện tại
            limitInfo.setCount(limitInfo.getCount() + 1);
        }
        feedbackLimits.put(username, limitInfo);
    }

    public List<FeedbackRequest> getFeedbacksByUsername(String username, String requesterUsername) {
        // Tìm Customer dựa trên username
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with username: " + username));

        // Kiểm tra vai trò của người yêu cầu
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isManager = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"));

        List<Feedback> feedbacks;
        if (isManager) {
            // Nếu là Manager, trả về tất cả Feedback (bao gồm cả Feedback đã ẩn)
            feedbacks = feedBackRepository.findByCustomer(customer);
        } else {
            // Nếu không phải Manager, kiểm tra xem có phải là Customer sở hữu Feedback không
            if (!username.equals(requesterUsername)) {
                throw new IllegalArgumentException("You are not authorized to view this customer's feedbacks");
            }

            // Chỉ trả về Feedback chưa bị ẩn
            feedbacks = feedBackRepository.findByCustomerNotHidden(customer);
        }

        return feedbacks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Trả về tất cả feedback
    public List<FeedbackRequest> getAllFeedbacks(String requesterUsername) {
        // Kiểm tra vai trò của người yêu cầu
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isManager = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"));

        List<Feedback> feedbacks;
        if (isManager) {
            // Nếu là Manager, trả về tất cả Feedback (bao gồm cả Feedback đã ẩn)
            feedbacks = feedBackRepository.findAllIncludingHidden();
        } else {
            // Nếu không phải Manager, chỉ trả về Feedback chưa bị ẩn
            feedbacks = feedBackRepository.findAllNotHidden();
        }

        return feedbacks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    //update feedback
    public void updateFeedback(Long id, FeedbackRequest feedbackRequest) {
        // Tìm Feedback theo id
        Feedback feedback = feedBackRepository.findByIdNotHidden(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        // Lấy thông tin người dùng từ SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Tìm Customer dựa trên username
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with username: " + username));

        // Kiểm tra xem Customer có phải là chủ sở hữu của Feedback không
        if (!feedback.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("You are not authorized to update this feedback");
        }

        // Cập nhật các trường của Feedback
        feedback.setSubject(feedbackRequest.getSubject());
        feedback.setComment(feedbackRequest.getMessage());
        feedback.setRating(feedbackRequest.getRating()); // Cập nhật rating
        feedback.setCustomer(customer);

        // Lưu Feedback đã cập nhật
        feedBackRepository.save(feedback);
    }

    // Delete Feedback
    public void deleteFeedback(Long id, boolean isPermanent) {
        // Lấy thông tin người dùng từ SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        boolean isManager = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"));

        Feedback feedback;
        if (isManager) {
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
        feedback = feedBackRepository.findByIdNotHidden(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

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

    public void unhideFeedback(Long id) {
        // Tìm Feedback theo id
        Feedback feedback = feedBackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));

        // Kiểm tra vai trò của người yêu cầu
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isManager = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"));

        if (!isManager) {
            throw new IllegalArgumentException("Only managers can unhide feedback");
        }

        // Hiển thị lại Feedback
        feedback.setHidden(false);
        feedBackRepository.save(feedback);
    }

    // Chuyển đổi từ Feedback sang FeedbackRequest
    private FeedbackRequest convertToDTO(Feedback feedback) {
        FeedbackRequest dto = new FeedbackRequest();
        dto.setId(feedback.getId()); // Thêm id
        dto.setSubject(feedback.getSubject());
        dto.setMessage(feedback.getComment());
        dto.setRating(feedback.getRating());
//        dto.setUsername(feedback.getCustomer().getUsername()); // Thêm username
//        dto.setHidden(feedback.isHidden());
        return dto;
    }
}
