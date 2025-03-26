package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.FeedbackDTO;
import coderuth.k23.skincare_booking.models.*;
import coderuth.k23.skincare_booking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedBackRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SpaServiceRepository spaServiceRepository;

    // Create Feedback
    public void createFeedback(FeedbackDTO feedbackDTO) {
        // Kiểm tra Customer có tồn tại không
        if (!customerRepository.findByUsernameAndEmail(feedbackDTO.getUsername(), feedbackDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer does not exist with username: " + feedbackDTO.getUsername() + " and email: " + feedbackDTO.getEmail());
        }

        // Kiểm tra SpaService có tồn tại không
        if (!spaServiceRepository.existsById(feedbackDTO.getSpaServiceId())) {
            throw new IllegalArgumentException("SpaService does not exist with ID: " + feedbackDTO.getSpaServiceId());
        }

        // Tìm Customer
        Customer customer = customerRepository.findByUsernameAndEmail(feedbackDTO.getUsername(), feedbackDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found")); // Đã kiểm tra ở trên, dòng này để đảm bảo

        SpaService spaService = spaServiceRepository.findById(feedbackDTO.getSpaServiceId())
                .orElseThrow(() -> new IllegalArgumentException("SpaService not found"));


        // Tạo Feedback mới
        Feedback feedback = new Feedback();
        feedback.setSubject(feedbackDTO.getSubject());
        feedback.setComment(feedbackDTO.getMessage());
        feedback.setCustomer(customer);
        feedback.setSpaService(spaService);


        // Lưu Feedback
        feedBackRepository.save(feedback);
    }
}