package org.example.goodreads.user;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    void registerUser(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        } else if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already registered");
        }

        String passwordHash = User.hashPassword(password);
        User newUser = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .build();
        this.userRepository.save(newUser);
    }

    public String validateUser(String identifier, String password) {
        boolean isEmail = identifier.contains("@");

        return (isEmail ? userRepository.findByEmail(identifier) : userRepository.findByUsername(identifier))
                .map(user -> {
                    if (user.verifyPassword(password)) {
                        return "Password is correct for user: " + user.getUsername();
                    } else {
                        throw new IllegalArgumentException("Invalid password");
                    }
                })
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public void deleteUser(Long userId) {
        Optional<User> userOptional = this.userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            this.userRepository.deleteById(userOptional.get().getUserId());
        } else {
            throw new NoSuchElementException("User not found");
        }
    }
}
