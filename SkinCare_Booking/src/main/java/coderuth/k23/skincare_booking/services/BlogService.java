package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.Blog;
import java.util.List;
import java.util.Optional;

public abstract class BlogService {
    public abstract List<Blog> getAllBlogs();
    public abstract Optional<Blog> getBlogById(Long id);
    public abstract Blog saveBlog(Blog blog);
    public abstract Blog updateBlog(Long id, Blog blog);
    public abstract void deleteBlog(Long id);
}
