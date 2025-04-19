package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.BlogRequestDTO;
import coderuth.k23.skincare_booking.dtos.response.BlogResponseDTO;
import coderuth.k23.skincare_booking.models.Blog;
import coderuth.k23.skincare_booking.repositories.BlogRepository;
import coderuth.k23.skincare_booking.util.BlogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }


    public List<BlogResponseDTO> getAllBlogs() {
        return blogRepository.findAll()
                .stream()
                .map(BlogMapper::toDto)
                .collect(Collectors.toList());
    }


    public BlogResponseDTO createBlog(BlogRequestDTO dto) {
        Blog blog = new Blog();
        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setAuthor(dto.getAuthor());
        blog.setCategory(dto.getCategory());
        blog.setDate(dto.getDate());
        blog.setExcerpt(dto.getExcerpt());
        blog.setImageUrl(dto.getImageUrl());
        LocalDateTime now = LocalDateTime.now();
        blog.setCreatedAt(now);
        blog.setUpdatedAt(now);

        Blog saved = blogRepository.save(blog);
        return BlogMapper.toDto(saved);
    }


    public BlogResponseDTO updateBlog(Long id, BlogRequestDTO dto) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id = " + id));

        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setAuthor(dto.getAuthor());
        blog.setCategory(dto.getCategory());
        blog.setDate(dto.getDate());
        blog.setExcerpt(dto.getExcerpt());
        blog.setImageUrl(dto.getImageUrl());
        blog.setUpdatedAt(LocalDateTime.now());

        Blog updated = blogRepository.save(blog);
        return BlogMapper.toDto(updated);
    }


    public void deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new RuntimeException("Blog not found with id = " + id);
        }
        blogRepository.deleteById(id);
    }
}
