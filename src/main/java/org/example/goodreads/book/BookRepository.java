package org.example.goodreads.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByISBN(String isbn);
    Optional<Book> findByBookId(long bookId);
    List<Book> findByTitle(String title);
    void deleteByBookId(Long bookId);
    List<Book> findByTitleContainingIgnoreCase(String title);
}
