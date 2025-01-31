package org.example.goodreads.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
public class BookDto {
    private long bookId;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^[0-9-]+$", message = "ISBN must contain only digits and '-'")
    @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
    private String ISBN;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @Past(message = "Release date must be in past")
    @NotNull(message = "Release date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @URL(message = "Invalid URL format")
    private String purchaseLink;

    public BookDto(Book book) {
        this.bookId = book.getBookId();
        this.ISBN = book.getISBN();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.releaseDate = book.getReleaseDate();
        this.description = book.getDescription();
        this.purchaseLink = book.getPurchaseLink();
    }
}
