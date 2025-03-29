package coderuth.k23.skincare_booking.dtos.response;

import lombok.Data;

@Data
public class SpaServiceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int duration;
}
