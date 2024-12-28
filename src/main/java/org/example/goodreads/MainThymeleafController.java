package org.example.goodreads;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainThymeleafController {
    @GetMapping("/")
    public String home() {
        return "mainPage";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
