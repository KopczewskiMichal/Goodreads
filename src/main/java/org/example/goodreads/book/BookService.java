package org.example.goodreads.book;

import java.util.List;
import java.util.NoSuchElementException;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findById(long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    void addBook(Book book) {
        if (bookRepository.findByISBN(book.getISBN()).isPresent()) throw new IllegalArgumentException("Book with this ISBN already exists");
        else {
            this.bookRepository.save(book);
        }
    }

    List<Book> getBookByTittle(String title) {
        return bookRepository.findByTitle(title);
    }
}
