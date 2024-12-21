package org.example.goodreads.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    public UserController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, Object> body) {
        String userName = body.get("username").toString();
        String password = body.get("password").toString();
        String email = body.get("email").toString();

        String passwordHash = User.hashPassword(password);
        User newUser = User.builder()
                .username(userName)
                .email(email)
                .passwordHash(passwordHash)
                .build();

        this.userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateUser(@RequestBody Map<String, Object> body) {
        String identifier = body.get("identifier").toString();
        String password = body.get("password").toString();
        
        boolean isEmail = identifier.contains("@");

        return (isEmail ? userRepository.findByEmail(identifier) : userRepository.findByUsername(identifier))
                .map(user -> {
                    if (user.verifyPassword(password)) {
                        return ResponseEntity.ok("Password is correct for user: " + user.getUsername());
                    } else {
                        return ResponseEntity.status(401).body("Invalid password");
                    }
                })
                .orElse(ResponseEntity.status(404).body("User not found"));
    }
}
