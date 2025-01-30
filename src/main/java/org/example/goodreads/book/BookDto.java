package org.example.goodreads.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class BookDto {

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^[0-9-]+$", message = "ISBN must contain only digits and '-'")
    @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
    private String ISBN;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Release date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @URL(message = "Invalid URL format")
    private String purchaseLink;

    private byte[] cover;

}
