package org.example.goodreads.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class ThymeleafAdminController {
    private final UserService userService;

    @Autowired
    public ThymeleafAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage(Model model) {
        model.addAttribute("usersCount", userService.getAllUsersCount());
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }
}
