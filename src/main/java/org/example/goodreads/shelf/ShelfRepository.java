package org.example.goodreads.shelf;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Optional<Shelf> findByShelfId(long id);
    List<Shelf> findByUser_UserId(long userId);
    List<Shelf> findByUser_UserIdAndBooks_BookId(long userId, long bookId);
}
