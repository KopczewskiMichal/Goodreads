package org.example.goodreads;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
@Table(name = "Genres")
public class Genre implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long genre_id;
    @Column(nullable = false)
    private String genreName;

    @Transient
    private static final ObjectMapper mapper = new ObjectMapper();

    @ManyToMany
    @JoinTable(name = "genre_book",
    joinColumns = @JoinColumn(name = "genre_id"),
    inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
        book.getGenres().add(this);
    }

}
