package org.example.goodreads.book;

import jakarta.validation.Valid;
import org.example.goodreads.Review.ReviewService;
import org.example.util.RolesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/books")
class ThymeleafBookController {
    private final BookService bookService;
    private final ReviewService reviewService;

    @Autowired
    ThymeleafBookController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("bookDto", new BookDto());  // Inicjalizacja pustego DTO
        return "addBook";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute BookDto bookDto,
                          BindingResult bindingResult,
                          @RequestParam(required = false) MultipartFile cover,
                          Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult);
            return "addBook";
        }

        try {

            byte[] coverBytes = null;
            if (cover != null && !cover.isEmpty()) {
                coverBytes = cover.getBytes();
            }

            Book book = Book.builder()
                    .ISBN(bookDto.getISBN())
                    .title(bookDto.getTitle())
                    .author(bookDto.getAuthor())
                    .releaseDate(bookDto.getReleaseDate())
                    .description(bookDto.getDescription())
                    .purchaseLink(bookDto.getPurchaseLink())
                    .cover(coverBytes)
                    .build();

            bookService.addBook(book);

            model.addAttribute("message", "Dodano książkę: " + bookDto.getTitle());
            model.addAttribute("book", book);

            return "redirect:/books/public";

        } catch (Exception e) {
            model.addAttribute("error", "Błąd podczas dodawania książki.");
            return "addBook";
        }
    }






    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "Książka o podanym ID nie istnieje.");
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        return "editBook";
    }


    @GetMapping("/public")
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("booksCount", bookService.getAllBooksCount());
        model.addAttribute("reviewsCount", reviewService.getAllReviewsCount());
        model.addAttribute("authority", RolesUtil.getRole());
        return "books";
    }

    @GetMapping("/public/{id}")
    public String showBookDetails(@PathVariable long id, Model model) {
        Book book = bookService.getBookById(id);

        if (book == null) {
            model.addAttribute("error", "Book with this Id doesn't exist.");
            return "redirect:/books/public";
        }
        model.addAttribute("book", book);
        model.addAttribute("authority", RolesUtil.getRole());
        return "bookDetails";
    }

    @GetMapping("/public/cover")
    public ResponseEntity<byte[]> getCover(@RequestParam Long bookId) {
        Book book = this.bookService.getBookById(bookId);
        byte[] image;
        if (book == null || book.getCover() == null) {
            image = getDefaultImage();
        } else {
            image = book.getCover();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    private byte[] getDefaultImage() {
        try {
            Resource resource = new ClassPathResource("/images/default_cover.png");
            return StreamUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default image", e);
        }
    }
}
