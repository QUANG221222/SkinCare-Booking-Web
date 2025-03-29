package coderuth.k23.skincare_booking.controllers.res;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import coderuth.k23.skincare_booking.services.SpaServiceService;
import coderuth.k23.skincare_booking.dtos.response.ApiResponse;
import coderuth.k23.skincare_booking.dtos.request.SpaServiceRequestDTO;
import coderuth.k23.skincare_booking.dtos.response.SpaServiceResponseDTO;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/spa-services")
public class SpaServiceController {

    @Autowired
    private SpaServiceService spaServiceService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<SpaServiceResponseDTO>> addSpaService(@Valid @RequestBody SpaServiceRequestDTO requestDTO) {
        Optional<SpaServiceResponseDTO> savedService = spaServiceService.addSpaService(requestDTO);
        return savedService.map(service -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Spa service created successfully", service)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Failed to create spa service", null)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpaServiceResponseDTO>> getSpaService(@PathVariable Long id) {
        Optional<SpaServiceResponseDTO> service = spaServiceService.getSpaServiceById(id);
        return service.map(value -> ResponseEntity.ok(ApiResponse.success("Spa service found", value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Spa service not found", null)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<SpaServiceResponseDTO>>> getAllSpaServices() {
        List<SpaServiceResponseDTO> services = spaServiceService.getAllSpaServices();
        return ResponseEntity.ok(ApiResponse.success("Spa services retrieved", services));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<SpaServiceResponseDTO>> updateSpaService(@PathVariable Long id, @Valid @RequestBody SpaServiceRequestDTO requestDTO) {
        Optional<SpaServiceResponseDTO> updatedService = spaServiceService.updateSpaService(id, requestDTO);
        return updatedService.map(service -> ResponseEntity.ok(ApiResponse.success("Spa service updated", service)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Spa service not found", null)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpaService(@PathVariable Long id) {
        boolean deleted = spaServiceService.deleteSpaService(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Spa service deleted"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Spa service not found", null));
        }
    }
}
