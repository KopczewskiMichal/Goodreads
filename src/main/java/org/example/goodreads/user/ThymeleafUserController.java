package org.example.goodreads.user;

import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafUserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ThymeleafUserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/user")
    public String userPage(Model model) {
        // TODO stworzyć model html
        //TODO dodać do modelu dane użytkownika

        return "userPage";
    }
}
