package org.example.goodreads.shelf;

import org.example.goodreads.book.Book;
import org.example.goodreads.book.BookRepository;
import org.example.goodreads.user.User;
import org.example.goodreads.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ShelfService {
    private final ShelfRepository shelfRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ShelfService(ShelfRepository shelfRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.shelfRepository = shelfRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<Shelf> getShelfByUserId(long userId) {
        List<Shelf> shelfs = shelfRepository.findByUser_UserId(userId);

        if (! shelfs.isEmpty()) {
            return shelfs;
        } else {
            throw new NoSuchElementException("Shelf not found for user ID: " + userId);
        }
    }

    public Shelf createShelfForUser(String shelfName, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found for user ID: " + userId));

        Shelf shelf = Shelf.builder()
                .shelfName(shelfName)
                .user(user)
                .build();
        return shelfRepository.save(shelf);
    }

    public Shelf addBookToShelf(long shelfId, long bookId) {
        Optional<Shelf> shelf = shelfRepository.findByShelfId(shelfId);
        Optional<Book> book = bookRepository.findByBookId(bookId);

        if (shelf.isPresent() && book.isPresent()) {
            shelf.get().addBook(book.get());
            return shelfRepository.save(shelf.get());
        } else {
            throw new NoSuchElementException("Either shelf or book not found for shelf ID: " + shelfId + ", book ID: " + bookId);
        }
    }
}
