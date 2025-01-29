package org.example.goodreads.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private String password;

    private String confirmPassword;


    UserDto (User user) {
        this.id = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.description = user.getDescription();
    }
}
