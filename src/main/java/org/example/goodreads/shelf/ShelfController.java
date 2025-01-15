package org.example.goodreads.shelf;

import jakarta.servlet.http.HttpServletRequest;
import org.example.goodreads.book.Book;
import org.example.goodreads.book.BookRepository;
import org.example.goodreads.user.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/shelves")
public class ShelfController {
    private final ShelfService shelfService;
    private final JwtUtil jwtUtil;


    @Autowired
    ShelfController(ShelfService shelfService, JwtUtil jwtUtil) {
        this.shelfService = shelfService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/get-by-user")
    public List<Shelf> getShelvesByUser(HttpServletRequest request) {
        long userId = jwtUtil.getUserIdFromRequest(request);
        return shelfService.getShelvesByUserId(userId);
    }

    @PostMapping("/add-on-shelf")
    public Shelf addBookOnShelf(
            @RequestParam("shelfId") long shelfId,
            @RequestParam("bookId") long bookId) {
        return shelfService.addBookOnShelf(bookId, shelfId);
    }

    @PostMapping("/remove-from-shelf")
    public Shelf removeBookFromShelf(
            @RequestParam("shelfId") long shelfId,
            @RequestParam("bookId") long bookId) {
        return shelfService.removeBookFromShelf(bookId, shelfId);
    }

    @PostMapping("/add-or-remove-book")
    public String addOrRemoveBook(
            @RequestParam("bookId") long bookId,
            @RequestParam("shelfIds") List<Long> shelfIds,
            HttpServletRequest request) {

        long userId = jwtUtil.getUserIdFromRequest(request);

        List<Shelf> currentShelves = shelfService.getUserShelvesWithBook(bookId, userId);

        for (long shelfId : shelfIds) {
            if (currentShelves.stream().noneMatch(shelf -> shelf.getShelfId() == shelfId)) {
                shelfService.addBookOnShelf(bookId, shelfId);
            }
        }

        for (Shelf shelf : currentShelves) {
            if (!shelfIds.contains(shelf.getShelfId())) {
                shelfService.removeBookFromShelf(bookId, shelf.getShelfId());
            }
        }

        return "redirect:/books/public/" + bookId;
    }


}
