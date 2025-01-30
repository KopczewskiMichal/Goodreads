package org.example.goodreads.book;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.example.goodreads.Review.Review;

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
@JsonIgnoreProperties(value = {"ISBN", "title", "author", "releaseDate", "description", "purchaseLink", "cover", "reviews"}, allowGetters = false)
public class Book implements Serializable {
    @Id
    @JsonProperty
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="book_id")
    private long bookId;
    @Column(unique=true, nullable = false)
    @JsonIgnore
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
    @Column(name = "cover")
    private byte[] cover;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonManagedReference
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Book(BookDto bookDto) {
        this.ISBN = bookDto.getISBN();
        this.title = bookDto.getTitle();
        this.author = bookDto.getAuthor();
        this.releaseDate = bookDto.getReleaseDate();
        this.description = bookDto.getDescription();
        this.purchaseLink = bookDto.getPurchaseLink();
        this.cover = bookDto.getCover();
    }
}
