package org.example.goodreads.user;

import jakarta.annotation.PostConstruct;
import org.example.goodreads.Review.ReviewService;
import org.example.goodreads.shelf.Shelf;
import org.example.goodreads.shelf.ShelfService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ShelfService shelfService;


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
        shelfService.createShelfForUser("Want to Read", result.getUserId());
        shelfService.createShelfForUser("Readed", result.getUserId());
        return result;
    }

    public User validateUser(String identifier, String password) {
        boolean isEmail = identifier.contains("@");

        return (isEmail ? userRepository.findByEmail(identifier) : userRepository.findByUsername(identifier))
                .map(user -> {
                    if (user.verifyPassword(password)) {
                        return user;
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

    public void updateUserPassword (long userId, String password) {
        Optional<User> userOptional = this.userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String passwordHash = User.hashPassword(password);
            user.setPasswordHash(passwordHash);
            this.userRepository.save(user);
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

    public void updateUser(UserDto userDto) {
        Optional<User> userOptional = this.userRepository.findByUserId(userDto.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setDescription(userDto.getDescription());
            this.userRepository.save(user);
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

    public boolean userExists(String username, String email) {
        return userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent();
    }

    User registerFromFile(String fileContent) throws IllegalArgumentException {
        try {
            JSONObject json = new JSONObject(fileContent);
            String username = json.getString("username");
            String passwordHash = json.getString("passwordHash");
            String email = json.getString("email");
            String description = json.optString("description", "");
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already registered");
            } else if (userRepository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Username already registered");
            }
            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .passwordHash(passwordHash)
                    .description(description)
                    .build();
            User user = this.userRepository.save(newUser);
            JSONArray shelvesArray = json.getJSONArray("shelves");
            for (int i = 0; i < shelvesArray.length(); i++) {
                JSONObject shelfJsonObj = shelvesArray.getJSONObject(i);
                String shelfName = shelfJsonObj.getString("shelfName");
                Shelf shelf = shelfService.createShelfForUser(shelfName, user.getUserId());
                long shelfId = shelf.getShelfId();
                JSONArray booksOnShelf = shelfJsonObj.getJSONArray("books");
                for (int j = 0; j < booksOnShelf.length(); j++) {
                    JSONObject bookJsonObj = booksOnShelf.getJSONObject(j);
                    long bookId = bookJsonObj.getLong("bookId");
                    shelfService.addBookOnShelf(shelfId, bookId);
                }
            }
            return user;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("Invalid JSON file");
        }
    }

    public UserDto getDtoFromUserId(long userId) {
        return new UserDto(getUserById(userId));
    }

    public long getAllUsersCount() {return userRepository.count() - 1;} // Usuniętego użytkownika nalezy pominąć

    public List<User> getAllUsers() {return userRepository.findByEmailNot("deleted@user");}
}
