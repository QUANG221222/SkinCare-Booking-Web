package coderuth.k23.skincare_booking.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BlogRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content is too short")
    private String content;

    @NotBlank(message = "Author is required")
    @Size(min = 3, max = 50, message = "Author name must be between 3 and 50 characters")
    private String author;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    // Dùng LocalDate để đồng nhất với input type="date"
    private LocalDate date;

    @Size(max = 255, message = "Excerpt must not exceed 255 characters")
    private String excerpt;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
