package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Data
@AllArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id", nullable = false, unique = true)
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống!")
    @Size(min = 2, max = 100, message = "Tiêu đề phải từ 2 đến 100 ký tự!")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Nội dung không được để trống!")
    @Size(min = 10, message = "Nội dung phải có ít nhất 10 ký tự!")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor mặc định với thời gian tạo và cập nhật
    public Blog() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}