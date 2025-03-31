package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.dtos.response.CustomerInfoResponse;
import coderuth.k23.skincare_booking.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Endpoint để lấy thông tin cơ bản của tất cả khách hàng
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerInfoResponse>>> getAllCustomers() {
        List<CustomerInfoResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }
}