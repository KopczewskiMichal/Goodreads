package org.example.goodreads.Review;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.*;
import lombok.*;
import org.example.goodreads.book.Book;
import org.example.goodreads.user.User;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "review")
@JsonIgnoreProperties({"toString"})
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    @Setter(AccessLevel.NONE)
    private long reviewId;

    private String text;

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private short stars;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    private static final ObjectMapper mapper = new ObjectMapper();
    ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", stars=" + stars +
                ", bookId=" + book.getBookId() +
                ", userId=" +  user.getUserId()+
                ", userName=" + user.getUsername() +
                ", userBirthDate=" + user.getBirthDate() +
                '}';
    }
}
