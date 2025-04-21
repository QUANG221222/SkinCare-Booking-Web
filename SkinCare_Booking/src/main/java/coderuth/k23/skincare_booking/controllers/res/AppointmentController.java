package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/protected/manager/appointments")
@PreAuthorize("hasRole('MANAGER')")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/update-payment-method/{id}")
    public ResponseEntity<String> updatePaymentMethod(
            @PathVariable("id") Long appointmentId,
            @RequestBody UpdatePaymentMethodRequest request) {
        try {
            appointmentService.updatePaymentMethod(appointmentId, request.getPaymentMethod());
            return ResponseEntity.ok("Payment method updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating payment method: " + e.getMessage());
        }
    }
}

class UpdatePaymentMethodRequest {
    private String paymentMethod;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}