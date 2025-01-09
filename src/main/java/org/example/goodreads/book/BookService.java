package org.example.goodreads.book;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
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

    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream().limit(20).toList();
    }

    public Book getBookById(long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    public void updateBook(Book book) {
        if (bookRepository.findById(book.getBook_id()).isPresent()) {
            bookRepository.save(book);
        } else {
            throw new NoSuchElementException("Book not found");
        }
    }
}
