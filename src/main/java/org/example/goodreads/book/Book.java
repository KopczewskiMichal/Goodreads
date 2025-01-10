package org.example.goodreads.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.example.goodreads.Genre;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name= "book")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="book_id")
    private long bookId;
    @Column(unique=true, nullable = false)
    private String ISBN;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    private String description;
    private String purchaseLink;

    @Lob
    @Column(name = "cover", nullable = true)
    private byte[] cover;

    @Column(name = "is_approved", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isApproved = false;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ManyToMany(mappedBy = "books")
    private List<Genre> genres = new ArrayList<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getBooks().add(this);
    }

}
