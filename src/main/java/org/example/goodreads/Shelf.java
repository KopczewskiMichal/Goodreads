package org.example.goodreads;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString(includeFieldNames = true)
@Table(name = "shelf")
public class Shelf implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shelf_id;

    private String shelfName;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "shelf_book",
    joinColumns = @JoinColumn(name = "shelf_id"),
    inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }
}
