package coderuth.k23.skincare_booking.until;

import coderuth.k23.skincare_booking.dtos.response.BlogResponseDTO;
import coderuth.k23.skincare_booking.models.Blog;

public class BlogMapper {

    public static BlogResponseDTO toDto(Blog blog) {
        BlogResponseDTO dto = new BlogResponseDTO();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setAuthor(blog.getAuthor());
        dto.setCategory(blog.getCategory());
        dto.setDate(blog.getDate());
        dto.setExcerpt(blog.getExcerpt());
        dto.setImageUrl(blog.getImageUrl());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        return dto;
    }
}
