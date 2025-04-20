package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.Payment;
import coderuth.k23.skincare_booking.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RevenueService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Map<String, Double> calculateMonthlyRevenueForCurrentYear() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            LocalDate startOfMonth = LocalDate.of(currentYear, month, 1);
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            LocalDateTime startDateTime = startOfMonth.atStartOfDay();
            LocalDateTime endDateTime = endOfMonth.atTime(LocalTime.MAX);

            List<Payment> paidPayments = paymentRepository.findByCreatedAtBetweenAndPaymentStatus(
                    startDateTime,
                    endDateTime,
                    Payment.PaymentStatus.PAID
            );

            double revenue = paidPayments.stream()
                    .mapToDouble(Payment::getAmount)
                    .sum();

            String monthLabel = "Month " + month;
            monthlyRevenue.put(monthLabel, revenue);
        }

        return monthlyRevenue;
    }
}

