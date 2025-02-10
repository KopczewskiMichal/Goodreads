package org.example.goodreads.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReviewId(long reviewId);
    List<Review> findByBook_BookId(long bookId);
    List<Review> findByUser_UserId(long userId);
}

