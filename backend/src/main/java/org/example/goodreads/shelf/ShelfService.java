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
        if (shelfName.isEmpty()) {
            throw new IllegalArgumentException("Shelf name cannot be empty.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found for user ID: " + userId));
        if (shelfRepository.existsByUserAndShelfName(user, shelfName)) {
            throw new IllegalStateException("A shelf with the same name already exists for this user.");
        }

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
            Shelf existingShelf = shelf.get();
            Book existingBook = book.get();

            if (existingShelf.getBooks().contains(existingBook)) {
                throw new IllegalStateException("The book is already on the shelf.");
            }

            existingShelf.addBook(existingBook);
            return shelfRepository.save(existingShelf);
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

    public List<Book> getBooksByShelfId(long shelfId) {
        // Dopuszczamy pustą półkę
        return shelfRepository.findBooksByShelfId(shelfId);
    }

    public void deleteShelfById(long shelfId) {
        shelfRepository.deleteById(shelfId);
    }

    public boolean doesShelfBelongsToUser(long shelfId, long userId) {
        Shelf shelf = shelfRepository.findById(shelfId).orElse(null);
        return shelf!= null && shelf.getUser().getUserId() == userId;
    }
}
