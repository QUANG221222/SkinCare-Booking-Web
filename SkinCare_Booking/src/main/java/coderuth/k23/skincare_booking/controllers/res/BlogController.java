package coderuth.k23.skincare_booking.controllers.res;

import coderuth.k23.skincare_booking.models.Blog;
import coderuth.k23.skincare_booking.services.BlogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // List all blogs - cho phép mọi người xem
    @GetMapping
    public String listBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "blog/blog_list";
    }

    // View blog details - cho phép mọi người xem
    @GetMapping("/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlogById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
        model.addAttribute("blog", blog);
        return "blog/blog_detail";
    }

    // Show form to create new blog - chỉ cho admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("blog", new Blog());
        return "blog/blog_form";
    }

    // Process create blog - chỉ cho admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createBlog(@Valid @ModelAttribute("blog") Blog blog, BindingResult result) {
        if (result.hasErrors()) {
            return "blog/blog_form";
        }
        blogService.saveBlog(blog);
        return "redirect:/blogs";
    }

    // Show form to edit blog - chỉ cho admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlogById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
        model.addAttribute("blog", blog);
        return "blog/blog_form";
    }

    // Process update blog - chỉ cho admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable Long id, @Valid @ModelAttribute("blog") Blog blog, BindingResult result) {
        if (result.hasErrors()) {
            return "blog/blog_form";
        }
        blogService.updateBlog(id, blog);
        return "redirect:/blogs";
    }

    // Delete blog - chỉ cho admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return "redirect:/blogs";
    }
}
