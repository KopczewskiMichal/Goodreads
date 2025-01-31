package org.example.goodreads.book;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    long getAllBooksCount() {return bookRepository.count();}

    public Book getBookById(long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    public void updateBook(BookDto bookDto, MultipartFile cover) throws IOException {
        Book book = bookRepository.findById(bookDto.getBookId())
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setReleaseDate(bookDto.getReleaseDate());
        book.setDescription(bookDto.getDescription());
        book.setPurchaseLink(bookDto.getPurchaseLink());
        if (cover != null && !cover.isEmpty()) {
            book.setCover(cover.getBytes());
        }
        bookRepository.save(book);
    }

}

