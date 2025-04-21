package coderuth.k23.skincare_booking.dtos.response;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BlogResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String category;
    private LocalDate date;
    private String excerpt;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Setter
    private int commentCount;      // ← thêm trường này


    public int getCommentCount() {
        return commentCount;
    }
}

