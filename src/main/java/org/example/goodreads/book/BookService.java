package org.example.goodreads.book;

import jakarta.transaction.Transactional;
import org.example.goodreads.shelf.Shelf;
import org.example.goodreads.shelf.ShelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ShelfRepository shelfRepository;


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

    @Transactional
    public void deleteBookById(Long bookId) {
        List<Shelf> shelvesWithBook = shelfRepository.findByBooksBookId(bookId);

        shelvesWithBook.stream()
                .peek(shelf -> shelf.getBooks().removeIf(book -> book.getBookId() == bookId)) // usuwamy książkę ze półki
                .forEach(shelfRepository::save);
        bookRepository.deleteById(bookId);
    }

    public List<Book> findBooksByTitleContaining(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public long countShelvesContainingBook(Long bookId) {
        return shelfRepository.countShelvesContainingBook(bookId);
    }

    public long countShelvesContainingBookWithName(Long bookId, String shelfName) {
        return shelfRepository.countShelvesContainingBookWithName(bookId, shelfName);
    }

}

