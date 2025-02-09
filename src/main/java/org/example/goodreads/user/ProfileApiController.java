package org.example.goodreads.user;

import jakarta.servlet.http.HttpServletRequest;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/user/profile")
public class ProfileApiController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;


    @GetMapping("download-profile/{id}")
    public ResponseEntity<InputStreamResource> downloadProfile(@PathVariable("id") long userId) {
        byte[] contentBytes = userService.getSerializedUserData(userId).getBytes();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=myGoodreadsProfile.json")
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(new InputStreamResource(byteArrayInputStream));
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteProfile(HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        }
    }



}
