package org.example.goodreads.shelf;

import org.example.goodreads.book.Book;
import org.example.goodreads.book.BookRepository;
import org.example.goodreads.user.User;
import org.example.goodreads.user.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Shelf> getShelvesByUserId(long userId) {
        List<Shelf> shelves = shelfRepository.findByUser_UserId(userId);

        if (! shelves.isEmpty()) {
            return shelves;
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

    public Shelf addBookOnShelf(long shelfId, long bookId) {
        Optional<Shelf> shelf = shelfRepository.findByShelfId(shelfId);
        Optional<Book> book = bookRepository.findByBookId(bookId);

        if (shelf.isPresent() && book.isPresent()) {
            shelf.get().addBook(book.get());
            System.out.println("Book added");
            System.out.println(shelf.get().getBooks());
            return shelfRepository.save(shelf.get());
        } else {
            throw new NoSuchElementException("Either shelf or book not found for shelf ID: " + shelfId + ", book ID: " + bookId);
        }
    }

    public List<Shelf> getUserShelvesWithBook(long bookId, long userId) {
        return shelfRepository.findByUser_UserIdAndBooks_BookId(userId, bookId);
    }

    public Shelf removeBookFromShelf(long bookId, long shelfId) {
        Shelf shelf = shelfRepository.findById(shelfId).orElseThrow(() ->
                new NoSuchElementException("Shelf not found with id " + shelfId));
        shelf.getBooks().removeIf(book -> book.getBookId() == bookId);
        return shelfRepository.save(shelf);
    }

    public List<DtoShelf> getShelvesByUserIdAndBookId(long userId, long bookId) {
        List<Shelf> shelves = this.getShelvesByUserId(userId);
        return shelves.stream()
                .map(shelf -> new DtoShelf(
                        shelf.getShelfId(),
                        shelf.getShelfName(),
                        shelf.getBooks().stream().anyMatch(book -> book.getBookId() == bookId)
                ))
                .collect(Collectors.toList());
    }


}
