package org.example.goodreads.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.goodreads.shelf.ShelfService;
import org.example.util.JwtUtil;
import org.example.util.RolesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
@RequestMapping("/profile")
public class ThymeleafProfileController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ShelfService shelfService;

    @Autowired
    public ThymeleafProfileController(UserService userService, JwtUtil jwtUtil, ShelfService shelfService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.shelfService = shelfService;
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

    @GetMapping("/edit")
    public String editUserPage(Model model, HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        UserDto userDto = new UserDto(userService.getUserById(userId));
        model.addAttribute("userDto", userDto);
        return "editUser";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("userDto") @Valid UserDto userDto,
                             BindingResult bindingResult, Model model, HttpServletRequest request) {
        userDto.setId(jwtUtil.getUserIdFromRequest(request)); // zabezpieczenie przed edycją innego użytkownika

        model.addAttribute("userDto", userDto);
        if (bindingResult.hasErrors()) {
            return "editUser";
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            if (userDto.getPassword().length() < 8) {
                model.addAttribute("error", "Password must be at least 8 characters long.");
                userService.updateUserPassword(userDto.getId(), userDto.getPassword());
                return "editUser";
            }
        }

        userService.updateUser(userDto);
        return "redirect:/profile/";
    }

    @PostMapping("/add-shelf")
    public String addShelf(@RequestParam("shelfName") String shelfName,
                            HttpServletRequest request) {
        shelfService.createShelfForUser(shelfName, jwtUtil.getUserIdFromRequest(request));
        return "redirect:/profile/";
    }


}
