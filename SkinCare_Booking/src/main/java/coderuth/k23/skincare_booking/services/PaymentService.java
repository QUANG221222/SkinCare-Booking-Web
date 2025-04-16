package coderuth.k23.skincare_booking.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import coderuth.k23.skincare_booking.models.Payment;
import coderuth.k23.skincare_booking.repositories.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Lấy danh sách tất cả các thanh toán
    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }
}