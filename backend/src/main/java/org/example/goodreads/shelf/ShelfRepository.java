package org.example.goodreads.shelf;

import jakarta.transaction.Transactional;
import org.example.goodreads.book.Book;
import org.example.goodreads.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Optional<Shelf> findByShelfId(long id);
    List<Shelf> findByUser_UserId(long userId);
    List<Shelf> findByUser_UserIdAndBooks_BookId(long userId, long bookId);

    @Query("SELECT s.books FROM Shelf s WHERE s.shelfId = :shelfId")
    List<Book> findBooksByShelfId(@Param("shelfId") long shelfId);

    boolean existsByUserAndShelfName(User user, String shelfName);
    List<Shelf> findByBooksBookId(Long bookId);

    @Query("SELECT COUNT(s) FROM Shelf s JOIN s.books b WHERE b.bookId = :bookId")
    long countShelvesContainingBook(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(s) FROM Shelf s JOIN s.books b WHERE b.bookId = :bookId AND s.shelfName = :shelfName")
    long countShelvesContainingBookWithName(@Param("bookId") Long bookId, @Param("shelfName") String shelfName);
}
