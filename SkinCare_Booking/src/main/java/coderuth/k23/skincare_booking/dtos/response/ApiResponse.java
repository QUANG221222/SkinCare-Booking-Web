package coderuth.k23.skincare_booking.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // This will exclude null fields from JSON output
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T payload;

    // Constructor without payload for responses that don't need data
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.payload = null;
    }

    // Static factory methods for common response patterns
    public static <T> ApiResponse<T> success(String message, T payload) {
        return new ApiResponse<>(true, message, payload);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    public static <T> ApiResponse<T> created(String message) {
            return new ApiResponse<>(true, message);
        }
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

    public static <T> ApiResponse<T> error(String message, T payload) {
        return new ApiResponse<>(false, message, payload);
    }
}
