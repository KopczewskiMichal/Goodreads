package org.example.goodreads.user;

import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            long userId = userService.validateUser(identifier, password);
            String jwt = jwtUtil.generateToken(userId);

            Cookie cookie = new Cookie("JWT", jwt);
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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpServletResponse response) {
        try {
            if (userService.userExists(username, email)) {
                return ResponseEntity.status(409).body("User with this username or password already exists");
            }

            User registeredUser = userService.registerUser(username, email, password);
            String jwt = jwtUtil.generateToken(registeredUser.getUserId());
            Cookie cookie = new Cookie("JWT", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            return ResponseEntity.ok("Registration successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register-from-file")
    public ResponseEntity<String> registerUserFromFile(
            @RequestParam("file") MultipartFile file,
            HttpServletResponse response) {
        try {
            User registeredUser = userService.registerFromFile(new String(file.getBytes(), StandardCharsets.UTF_8));

            String jwt = jwtUtil.generateToken(registeredUser.getUserId());
            Cookie cookie = new Cookie("JWT", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            return ResponseEntity.ok("Registration from file successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registering user from file: " + e.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        response.sendRedirect("/");
        return ResponseEntity.ok("Logged out successfully");
    }
}
