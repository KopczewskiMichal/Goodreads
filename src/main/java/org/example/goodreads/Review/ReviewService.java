package org.example.goodreads.Review;

import org.example.goodreads.book.Book;
import org.example.goodreads.book.BookRepository;
import org.example.goodreads.user.User;
import org.example.goodreads.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public void addReview(Long bookId, Long userId, String text, short stars) {
        if (stars <= 0 || stars > 5) throw new IllegalArgumentException("Stars must be between 0 and 5");

        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) throw new IllegalArgumentException("Book not found");

        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) throw new IllegalArgumentException("User not found");

        Review review = Review.builder()
                .book(book.get())
                .user(user.get())
                .text(text)
                .stars(stars)
                .build();

        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        if (review.isPresent()) {
            reviewRepository.delete(review.get());
        } else {
            throw new NoSuchElementException("Review not found");
        }
    }

}
