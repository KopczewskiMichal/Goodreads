package org.example.goodreads.user;

import jakarta.servlet.http.HttpServletRequest;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ThymeleafProfileController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ThymeleafProfileController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/")
    public String userPage(Model model, HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "userPage";
    }
}
