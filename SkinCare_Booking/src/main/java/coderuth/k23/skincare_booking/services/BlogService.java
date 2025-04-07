package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.Blog;
import coderuth.k23.skincare_booking.repositories.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    // Lấy danh sách tất cả blog
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    // Tạo blog mới
    public Blog createBlog(Blog blog) {
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        return blogRepository.save(blog);
    }

    // Cập nhật blog
    public Blog updateBlog(Long id, Blog updatedBlog) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        blog.setTitle(updatedBlog.getTitle());
        blog.setContent(updatedBlog.getContent());
        blog.setUpdatedAt(LocalDateTime.now());
        return blogRepository.save(blog);
    }

    // Xóa blog
    public void deleteBlog(Long id, RedirectAttributes redirectAttributes) {
        blogRepository.deleteById(id);
    }
}