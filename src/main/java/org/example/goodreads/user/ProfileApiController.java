package org.example.goodreads.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;


@RestController
@RequestMapping("/api/user/profile")
public class ProfileApiController {
    private final UserService userService;
    @Autowired
    public ProfileApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("download-profile/{id}")
    public ResponseEntity<InputStreamResource> downloadProfile(@PathVariable("id") long userId) {
        byte[] contentBytes = userService.getSerializedUserData(userId).getBytes();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=myGoodreadsProfile.json")
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(new InputStreamResource(byteArrayInputStream));
    }

}
