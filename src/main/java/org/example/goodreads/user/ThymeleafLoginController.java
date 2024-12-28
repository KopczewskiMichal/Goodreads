package org.example.goodreads.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;

@Controller
public class ThymeleafLoginController {
    private final UserService userService;

    @Autowired
    public ThymeleafLoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Nazwa widoku Thymeleaf (login.html)
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("identifier") String identifier,
            @RequestParam("password") String password,
            Model model) {
        try {
            String result = userService.validateUser(identifier, password);
            model.addAttribute("message", result);
//            return "dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid password");
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "User not found");
        }
        return "login";
    }
}
