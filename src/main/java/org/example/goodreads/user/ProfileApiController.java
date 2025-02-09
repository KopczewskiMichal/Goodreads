package org.example.goodreads.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.goodreads.shelf.Shelf;
import org.example.goodreads.shelf.ShelfService;
import org.example.util.JwtUtil;
import org.example.util.RolesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/user/profile")
public class ProfileApiController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ShelfService shelfService;


    @GetMapping("download-profile/{id}") // używane do wczytania backupu profilu
    public ResponseEntity<InputStreamResource> downloadProfile(@PathVariable("id") long userId) {
        byte[] contentBytes = userService.getSerializedUserData(userId).getBytes();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=myGoodreadsProfile.json")
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(new InputStreamResource(byteArrayInputStream));
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> userPage(HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        User user = userService.getUserById(userId);
        String role = RolesUtil.getRole();
        long usersCount = userService.getAllUsersCount();

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("role", role);
        response.put("usersCount", usersCount);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteProfile(HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-as-admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProfileAsAdmin(@PathVariable("id") long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user-dto") // Jedyny sens istnienia tego to żeby nie pisać JSON od nowa do edycji
    public ResponseEntity<UserDto> getUserDto(HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new UserDto(user));
    }

    @PostMapping("/edit")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody @Valid UserDto userDto,
                                                          BindingResult bindingResult, HttpServletRequest request) {

        userDto.setId(jwtUtil.getUserIdFromRequest(request)); // Zabezpieczenie przed edycją innego użytkownika

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(errors);
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            if (userDto.getPassword().length() < 8) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Password must be at least 8 characters long.");
                return ResponseEntity.badRequest().body(errorResponse);
            } else {
                userService.updateUserPassword(userDto.getId(), userDto.getPassword());
            }
        }

        userService.updateUser(userDto);

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "User updated successfully");
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/add-shelf")
    public ResponseEntity<String> addShelf(@RequestParam("shelfName") String shelfName,
                           HttpServletRequest request) {
        try {
            shelfService.createShelfForUser(shelfName, jwtUtil.getUserIdFromRequest(request));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("Shelf " + shelfName + " successfully created");
    }

    @DeleteMapping("/delete-shelf/{shelf-id}")
    public ResponseEntity<String> deleteShelf(@PathVariable("shelf-id") long shelfId,
                                                   HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        try {
            if (shelfService.doesShelfBelongsToUser(shelfId, userId)) {
            shelfService.deleteShelfById(shelfId);
            return ResponseEntity.ok("Shelf " + shelfId + " successfully deleted");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("It isn't yor shelf");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
