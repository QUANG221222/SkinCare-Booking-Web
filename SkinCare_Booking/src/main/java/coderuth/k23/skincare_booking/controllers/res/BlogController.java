package coderuth.k23.skincare_booking.controllers.res;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import coderuth.k23.skincare_booking.models.Blog;
import coderuth.k23.skincare_booking.services.BlogService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    public String getAllBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "blog/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("blog", new Blog());
        return "blog/create";
    }

    @PostMapping
    public String createBlog(@Valid @ModelAttribute("blog") Blog blog,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "blog/create";
        }
        blogService.createBlog(blog);
        return "redirect:/blogs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlogById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        model.addAttribute("blog", blog);
        return "blog/edit";
    }

    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable Long id,
                             @Valid @ModelAttribute("blog") Blog blog,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            blog.setId(id);
            return "blog/edit";
        }
        blogService.updateBlog(id, blog);
        return "redirect:/blogs";
    }

    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return "redirect:/blogs";
    }
}