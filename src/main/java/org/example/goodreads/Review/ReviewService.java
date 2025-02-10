package org.example.goodreads.Review;

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

    public void handleDeleteUser(long userId) {
        List<Review> reviewsToUpdate = reviewRepository.findByUser_UserId(userId);
        Optional<User> mockUser = userRepository.findByUsername("Deleted User");
        if (mockUser.isEmpty()) {
            throw new NoSuchElementException("Mock for deleted user doesn't exist");
        }

        if (reviewsToUpdate != null && !reviewsToUpdate.isEmpty()) {
            for (Review review : reviewsToUpdate) {
                review.setUser(mockUser.get());
                reviewRepository.save(review);
            }
        }
    }

    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBook_BookId(bookId);
    }

    public Optional<Review> getReviewById(long reviewId) {
        return reviewRepository.findByReviewId(reviewId);

    }


    public long getAllReviewsCount() {return reviewRepository.count();}
}
