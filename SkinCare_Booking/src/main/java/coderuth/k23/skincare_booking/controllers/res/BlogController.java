package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.models.Blog;
import coderuth.k23.skincare_booking.services.BlogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // Hiển thị danh sách blog (Admin)
    @GetMapping
    public String listBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "admin/Blog/blog_list";
    }

    // Hiển thị form tạo blog mới
    @GetMapping("/new")
    public String showCreateBlogForm(Model model) {
        model.addAttribute("blog", new Blog());
        return "admin/Blog/blog_create";
    }

    // Tạo blog mới
    @PostMapping
    public String createBlog(@Valid @ModelAttribute("blog") Blog blog,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/Blog/blog_create";
        }
        blogService.createBlog(blog);
        redirectAttributes.addFlashAttribute("success", "Tạo bài viết thành công!");
        return "redirect:/api/blogs";
    }

    // Hiển thị form chỉnh sửa blog
    @GetMapping("/edit/{id}")
    public String showEditBlogForm(@PathVariable Long id, Model model) {
        Blog blog = blogService.getAllBlogs().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        model.addAttribute("blog", blog);
        return "admin/Blog/blog_edit";
    }

    // Cập nhật blog
    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable Long id,
                             @Valid @ModelAttribute("blog") Blog blog,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/Blog/blog_edit";
        }
        blogService.updateBlog(id, blog);
        redirectAttributes.addFlashAttribute("success", "Cập nhật bài viết thành công!");
        return "redirect:/api/blogs";
    }

    // Hiển thị trang xác nhận xóa blog
    @GetMapping("/delete/{id}")
    public String showDeleteBlogForm(@PathVariable Long id, Model model) {
        Blog blog = blogService.getAllBlogs().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        model.addAttribute("blog", blog);
        return "admin/Blog/blog_delete";
    }

    // Xóa blog
    @PostMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            blogService.deleteBlog(id, redirectAttributes);
            redirectAttributes.addFlashAttribute("success", "Xóa bài viết thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa bài viết!");
        }
        return "redirect:/api/blogs";
    }
}