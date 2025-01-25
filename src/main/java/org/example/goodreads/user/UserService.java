package org.example.goodreads.user;

import jakarta.annotation.PostConstruct;
import org.apache.coyote.Response;
import org.example.goodreads.Review.ReviewService;
import org.example.goodreads.shelf.Shelf;
import org.example.goodreads.shelf.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
class UserService {
    private final UserRepository userRepository;
    private final ReviewService reviewService;
    private final ShelfService shelfService;

    @Autowired
    public UserService(UserRepository userRepository, ReviewService reviewService, ShelfService shelfService) {
        this.userRepository = userRepository;
        this.reviewService = reviewService;
        this.shelfService = shelfService;
    }

    @PostConstruct
    public void init() {
        if (userRepository.findByUsername("Deleted User").isEmpty()) {
        registerUser("Deleted User", "deletedUser@example.com", "deletedUser");
        }
    }


    User registerUser(String username, String email, String password) {
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
        User result = this.userRepository.save(newUser);
        Shelf defaultShelf = shelfService.createShelfForUser("Want to Read", result.getUserId());
        System.out.println(defaultShelf.getShelfId());
        return result;
    }

    public long validateUser(String identifier, String password) {
        boolean isEmail = identifier.contains("@");

        return (isEmail ? userRepository.findByEmail(identifier) : userRepository.findByUsername(identifier))
                .map(user -> {
                    if (user.verifyPassword(password)) {
                        return user.getUserId();
                    } else {
                        throw new IllegalArgumentException("Invalid password");
                    }
                })
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public void deleteUser(Long userId) {
        Optional<User> userOptional = this.userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            reviewService.handleDeleteUser(userOptional.get().getUserId());
            userRepository.deleteById(userOptional.get().getUserId());
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

    public User getUserById(long userId) {
        Optional<User> userOptional = this.userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

    public String getSerializedUserData(long userId) {
        User user = this.getUserById(userId);
        try {
            return User.getMapper().writeValueAsString(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public boolean userExists(String username, String email) {
        return userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent();
    }
}
