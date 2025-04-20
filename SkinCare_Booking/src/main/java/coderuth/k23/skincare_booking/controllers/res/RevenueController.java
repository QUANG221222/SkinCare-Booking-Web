package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.services.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getMonthlyRevenueForCurrentYear() {
        try {
            Map<String, Double> revenue = revenueService.calculateMonthlyRevenueForCurrentYear();
            return ResponseEntity.ok(ApiResponse.success("Monthly revenue retrieved successfully", revenue));
        }
        catch (Exception e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        }
    }
}

