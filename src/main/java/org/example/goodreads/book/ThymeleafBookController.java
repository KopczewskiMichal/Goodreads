package org.example.goodreads.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/books")
class ThymeleafBookController {
    private final BookService bookService;

    @Autowired
    ThymeleafBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/add")
    public String addBook() {
        return "addBook";
    }

    @PostMapping("/add")
    public String addBook(@RequestParam String ISBN,
                          @RequestParam String title,
                          @RequestParam String author,
                          @RequestParam String releaseDate, // Data jako String
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) String purchaseLink,
                          @RequestParam(required = false) String photoUrl,
                          Model model) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedReleaseDate = dateFormat.parse(releaseDate);

            Book book = Book.builder()
                    .ISBN(ISBN)
                    .title(title)
                    .author(author)
                    .releaseDate(parsedReleaseDate)
                    .description(description)
                    .purchaseLink(purchaseLink)
                    .photoUrl(photoUrl)
                    .build();

            bookService.addBook(book);

            model.addAttribute("message", "Dodano książkę: " + title);
            model.addAttribute("book", book);

            return "redirect:/books";

        } catch (ParseException e) {
            model.addAttribute("error", "Błąd parsowania daty: " + releaseDate);
            return "add-book";
        }
    }

    @GetMapping("")
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }


}
