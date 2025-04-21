package coderuth.k23.skincare_booking.dtos.response;

import coderuth.k23.skincare_booking.dtos.response.VietQRData;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
public class VietQRResponse {
    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("data")
    private VietQRData data;
}