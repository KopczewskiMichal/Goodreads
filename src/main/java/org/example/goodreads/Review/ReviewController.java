package org.example.goodreads.Review;

import jakarta.servlet.http.HttpServletRequest;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
    public void deleteReview(@PathVariable("id") Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

}
