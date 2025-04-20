package coderuth.k23.skincare_booking.dtos.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VietQRData {
    @JsonProperty("qrDataURL")
    private String qrDataURL;
}