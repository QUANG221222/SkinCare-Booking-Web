package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.dtos.response.StaffInfoResponse;
import coderuth.k23.skincare_booking.dtos.response.UserInfoResponse;
import coderuth.k23.skincare_booking.services.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staffs")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // Endpoint để lấy thông tin cơ bản của tất cả nhân viên
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffInfoResponse>>> getAllStaff() {
        List<StaffInfoResponse> staff = staffService.getAllStaff();
        return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", staff));
    }
}

