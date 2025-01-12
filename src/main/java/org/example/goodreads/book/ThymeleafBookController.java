package org.example.goodreads.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
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

    @Autowired
    ThymeleafBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/public/add")
    public String addBook() {
        return "addBook";
    }

    @PostMapping("/public/add")
    public String addBook(@RequestParam String ISBN,
                          @RequestParam String title,
                          @RequestParam String author,
                          @RequestParam String releaseDate, // Data jako String
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) String purchaseLink,
                          @RequestParam(required = false) byte[] cover,
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
                    .cover(cover)
                    .build();

            bookService.addBook(book);

            model.addAttribute("message", "Dodano książkę: " + title);
            model.addAttribute("book", book);

            return "redirect:/books/public";

        } catch (ParseException e) {
            model.addAttribute("error", "Błąd parsowania daty: " + releaseDate);
            return "addBook";
        }
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam String author,
                           @RequestParam String releaseDate,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String purchaseLink,
                           @RequestParam(required = false) MultipartFile cover,
                           Model model) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedReleaseDate = dateFormat.parse(releaseDate);

            Book book = bookService.getBookById(id);
            if (book == null) {
                model.addAttribute("error", "Książka o podanym ID nie istnieje.");
                return "redirect:/books/public";
            }

            book.setTitle(title);
            book.setAuthor(author);
            book.setReleaseDate(parsedReleaseDate);
            book.setDescription(description);
            book.setPurchaseLink(purchaseLink);

            if (!cover.isEmpty()) {
                book.setCover(cover.getBytes());
            }

            bookService.updateBook(book);

            model.addAttribute("message", "Książka została zaktualizowana: " + title);
            model.addAttribute("book", book);

            return "redirect:/books/public";

        } catch (ParseException e) {
            model.addAttribute("error", "Błąd parsowania daty: " + releaseDate);
            return "editBook";
        } catch (IOException e) {
            model.addAttribute("error", "Błąd przesyłania pliku okładki.");
            return "editBook";
        }
    }

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
