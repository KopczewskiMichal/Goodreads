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

    public void deleteUser(String userId) {
        Optional<User> userOptional = this.userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            this.userRepository.deleteById(userOptional.get().getUser_id());
        } else {
            throw new NoSuchElementException("User not found");
        }
    }
}
