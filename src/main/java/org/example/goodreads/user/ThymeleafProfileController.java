package org.example.goodreads.user;

import jakarta.servlet.http.HttpServletRequest;
import org.example.util.JwtUtil;
import org.example.util.RolesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.NoSuchElementException;
import java.util.Objects;

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
        model.addAttribute("authority", RolesUtil.getRole());
        model.addAttribute("usersCount", userService.getAllUsersCount());

        return "userPage";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);

            if (Objects.equals(RolesUtil.getRole(), "ROLE_ADMIN")) {
                return "redirect:/admin/";
            } else {
            redirectAttributes.addFlashAttribute("message", "User deleted successfully.");
            return "redirect:/api/auth/logout";
            }
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/api/auth/logout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the user.");
            return "redirect:/api/auth/logout";
        }
    }
}
