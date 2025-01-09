package org.example.goodreads.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository {
    Optional<Review> findByReviewId(String reviewId);
    List<Review> findByBookId(String bookId);
}
