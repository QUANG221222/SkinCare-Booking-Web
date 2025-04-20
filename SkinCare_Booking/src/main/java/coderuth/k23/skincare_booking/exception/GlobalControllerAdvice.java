package coderuth.k23.skincare_booking.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addCurrentURI(HttpServletRequest request, Model model) {
        // Lấy URI hiện tại từ request, nếu null thì gán bằng chuỗi rỗng
        String currentURI = request.getRequestURI();
        if (currentURI == null) {
            currentURI = "";
        }
        model.addAttribute("currentURI", currentURI);
    }
}