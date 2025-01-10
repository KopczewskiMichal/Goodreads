package org.example.goodreads.Review;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.example.goodreads.book.Book;
import org.example.goodreads.user.User;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "review")
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewId;

    private String text;

    @Column(nullable = false)
    private short stars;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    private static final ObjectMapper mapper = new ObjectMapper();
}
