package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.models.Blog;
import coderuth.k23.skincare_booking.services.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blogs")
public class UserBlogController {

    @Autowired
    private BlogService blogService;

    // Hiển thị danh sách blog cho user
    @GetMapping
    public String listBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "user/blog_list";
    }

    // Hiển thị chi tiết bài viết
    @GetMapping("/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.getAllBlogs().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Blog not found!"));
        model.addAttribute("blog", blog);
        return "user/blog_detail";
    }
}