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

        Customer customer = customerRepository.findByUsernameAndEmail(feedbackDTO.getUsername(), feedbackDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));


        Feedback feedback = new Feedback();
        feedback.setSubject(feedbackDTO.getSubject());
        feedback.setComment(feedbackDTO.getMessage());
        feedback.setCustomer(customer);

        // Lưu Feedback
        feedBackRepository.save(feedback);
    }
}