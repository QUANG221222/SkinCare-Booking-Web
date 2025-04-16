package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.services.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blogs")
public class PublicBlogController {

    @Autowired
    private BlogService blogService;

    // Công khai danh sách blog cho người dùng
    @GetMapping
    public String listBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogsDTO());
        return "public/Blog/blog_list";
    }

    // Công khai xem chi tiết blog
    @GetMapping("/view/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.convertToResponseDTO(blogService.getBlogById(id)));
        return "public/Blog/blog_detail";
    }
}
