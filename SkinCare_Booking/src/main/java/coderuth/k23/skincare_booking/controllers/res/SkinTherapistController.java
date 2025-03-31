package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.dtos.response.SkinTherapistInfoResponse;
import coderuth.k23.skincare_booking.services.SkinTherapistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skin-therapists")
public class SkinTherapistController {

    @Autowired
    private SkinTherapistService skinTherapistService;

    // Endpoint để lấy thông tin cơ bản của tất cả SkinTherapist
    @GetMapping
    public ResponseEntity<ApiResponse<List<SkinTherapistInfoResponse>>> getAllSkinTherapists() {
        List<SkinTherapistInfoResponse> therapists = skinTherapistService.getAllSkinTherapists();
        return ResponseEntity.ok(ApiResponse.success("Skin therapists retrieved successfully", therapists));
    }
}
