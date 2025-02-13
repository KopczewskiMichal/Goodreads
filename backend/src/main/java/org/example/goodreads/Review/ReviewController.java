package org.example.goodreads.Review;

import jakarta.servlet.http.HttpServletRequest;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Controller
@RequestMapping("/review")
public class ReviewController {
private final ReviewService reviewService;
private final JwtUtil jwtUtil;

    @Autowired
    public ReviewController(ReviewService reviewService, JwtUtil jwtUtil) {
        this.reviewService = reviewService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable("id") long reviewId) {
        Optional<Review> res = reviewService.getReviewById(reviewId);
        if (res.isPresent()) {
            return ResponseEntity.ok(res.get());
        } else return (ResponseEntity<Review>) ResponseEntity.notFound();
    }

    @GetMapping("/public/get-by-bookId/{bookId}")
    public ResponseEntity<?> getByBookId(@PathVariable("bookId") long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId));
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<String> addReview(@PathVariable("id") Long bookId,
                                            @RequestParam String reviewText,
                                            @RequestParam String stars,
                                            HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        reviewService.addReview(bookId, userId, reviewText, Short.parseShort(stars));

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/books/public/{id}")
                .buildAndExpand(bookId)
                .toUri();
        return ResponseEntity.created(location).body("Review added");
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteReview(@PathVariable("id") long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().body("Review deleted");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteReviewApi(@PathVariable("id") long reviewId) {
        try {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().body("Review deleted");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
