    package coderuth.k23.skincare_booking.models;

    import jakarta.persistence.*;
    import lombok.Data;
    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Data
    @Entity
    @Table(name = "blog")
    public class Blog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String title;

        @Column(length = 10000)
        private String content;

        private String author;

        // Các trường bổ sung
        private String category;      // Danh mục của bài blog
        private LocalDate date;       // Ngày liên quan (ví dụ: ngày công bố)
        private String excerpt;       // Tóm tắt nội dung của bài blog
        private String imageUrl;      // Đường dẫn ảnh đại diện

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
