package org.example.goodreads;

import org.example.util.RolesUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafMainController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("authority", RolesUtil.getRole());
        return "mainPage";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
