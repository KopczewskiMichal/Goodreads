package org.example.goodreads.shelf;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.goodreads.book.Book;
import org.example.goodreads.book.BookRepository;
import org.example.goodreads.user.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shelves")
public class ShelfController {
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/get-by-user")
    @ResponseBody
    public List<Shelf> getShelvesByUser(HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        return shelfService.getShelvesByUserId(userId);
    }

    @GetMapping("/get-by-user-and-book/{bookId}")
    @ResponseBody
    public List<DtoShelf> getShelvesByUserAndBook(@PathVariable("bookId") long bookId, HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        return shelfService.getShelvesByUserIdAndBookId(userId, bookId);
    }

    @PostMapping("/add-on-shelf")
    public Shelf addBookOnShelf(
            @RequestParam("shelfId") long shelfId,
            @RequestParam("bookId") long bookId) {
        return shelfService.addBookOnShelf(shelfId, bookId);
    }

    @PostMapping("/api/add-on-shelf")
    public ResponseEntity<String> addBookOnShelfApi(
            @RequestParam("shelfId") long shelfId,
            @RequestParam("bookId") long bookId) {
        try {
        shelfService.addBookOnShelf(shelfId, bookId);
        return ResponseEntity.ok("Book added to shelf");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/remove-from-shelf")
    public Shelf removeBookFromShelf(
            @RequestParam("shelfId") long shelfId,
            @RequestParam("bookId") long bookId) {
        return shelfService.removeBookFromShelf(bookId, shelfId);
    }

    @PostMapping("/api/remove-from-shelf")
    public ResponseEntity<String> removeBookFromShelfApi(
            @RequestParam("shelfId") long shelfId,
            @RequestParam("bookId") long bookId) {
        try {
            shelfService.removeBookFromShelf(bookId, shelfId);
            return ResponseEntity.ok("Book removed from shelf");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add-or-remove-book")
    public String addOrRemoveBook(@RequestBody List<DtoShelf> shelves, @RequestParam("bookId") long bookId, HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);

        List<Shelf> currentShelves = shelfService.getUserShelvesWithBook(bookId, userId);

        for (DtoShelf dtoShelf : shelves) {
            if (dtoShelf.isBookOnShelf() && currentShelves.stream().noneMatch(s -> s.getShelfId() == dtoShelf.getShelfId())) {
                shelfService.addBookOnShelf(dtoShelf.getShelfId(), bookId);
            } else if (!dtoShelf.isBookOnShelf() && currentShelves.stream().anyMatch(s -> s.getShelfId() == dtoShelf.getShelfId())) {
                shelfService.removeBookFromShelf(bookId, dtoShelf.getShelfId());
            }
        }

        return "redirect:/books/public/" + bookId;
    }
}
