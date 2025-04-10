package coderuth.k23.skincare_booking.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
  
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        // Thêm thông báo lỗi vào model để hiển thị trên giao diện
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error-page"; // Trả về tên template hiển thị lỗi
    }
       // Xử lý IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error-page"; // Trả về template hiển thị lỗi
    }

    // Xử lý ngoại lệ mặc định (fallback)
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        return "error/error-page"; // Trả về template hiển thị lỗi
    }
}
