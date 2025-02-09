package org.example.goodreads.user;

import jakarta.validation.Valid;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
            User user = userService.validateUser(identifier, password);
            String jwt = jwtUtil.generateToken(user.getUserId(), user.isAdmin());

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
            generateJwtCookie(response, registeredUser);

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
            generateJwtCookie(response, registeredUser);

            return ResponseEntity.ok("Registration from file successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registering user from file: " + e.getMessage());
        }
    }

    private void generateJwtCookie(HttpServletResponse response, User registeredUser) {
        String jwt = jwtUtil.generateToken(registeredUser.getUserId(), false);
        Cookie cookie = new Cookie("JWT", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
//        response.sendRedirect("/");
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/json-register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            if (userService.userExists(userDto.getUsername(), userDto.getEmail())) {
                return ResponseEntity.status(409).body("User with this username or email already exists");
            }

            User registeredUser = userService.registerUser(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
            generateJwtCookie(response, registeredUser);

            return ResponseEntity.ok("Registration successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

}

