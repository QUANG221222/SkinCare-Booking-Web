package coderuth.k23.skincare_booking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class StaffInfoResponse {
    private String fullname;
    private String phone;
    private String email;
    private String img;
    private String userType;
}