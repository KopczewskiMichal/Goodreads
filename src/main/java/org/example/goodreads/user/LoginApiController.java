package org.example.goodreads.user;

import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
public class LoginApiController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginApiController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestParam("identifier") String identifier,
            @RequestParam("password") String password,
            HttpServletResponse response) {
        try {
            String username = userService.validateUser(identifier, password);

            String jwt = jwtUtil.generateToken(username);

            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            return ResponseEntity.ok("Login successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid password");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Zmień na `true` w środowisku produkcyjnym
        cookie.setPath("/");
        cookie.setMaxAge(0); // Usunięcie ciasteczka przez ustawienie 0 czasu życia
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }
}
