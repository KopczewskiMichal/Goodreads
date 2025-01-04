package org.example.goodreads.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafLoginController {

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginApiEndpoint", "/api/auth/login");
        return "login";
    }
}
