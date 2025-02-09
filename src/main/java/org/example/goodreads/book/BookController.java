package org.example.goodreads.book;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private ReviewService reviewService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("bookDto", new BookDto());
        return "addBook";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute BookDto bookDto,
                          BindingResult bindingResult,
                          @RequestParam(required = false) MultipartFile cover,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookDto", bookDto);
            model.addAttribute("org.springframework.validation.BindingResult.bookDto", bindingResult);
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
            model.addAttribute("error", "Book with the given ID does not exist.");
            return "redirect:/books/public";
        }
        BookDto bookDto = new BookDto(book);
        model.addAttribute("bookDto", bookDto);
        model.addAttribute("bookId", id);
        return "editBook";
    }

    @GetMapping("/public/get-as-dto/{id}") // Pobranie jako DTO do późniejszej edycji itp.
    public ResponseEntity<String> getAsDto(@PathVariable Long id){
        try {
            Book book = bookService.getBookById(id);
            if (book == null) {
                return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
            }
            BookDto bookDto = new BookDto(book);
            return new ResponseEntity<>(bookDto.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String updateBook(@ModelAttribute("bookDto") @Valid BookDto bookDto, BindingResult bindingResult, @RequestParam("cover") MultipartFile coverFile, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookDto", bookDto);
            return "editBook";
        }

        try {
            bookService.BulkUpdateBook(bookDto, coverFile);
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while updating the book.");
            System.out.println(e.getMessage());
            return "editBook";
        }

        return "redirect:/books/public";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/edit")
    public ResponseEntity<?> updateBook(@RequestBody @Valid BookDto bookDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            bookService.updateBook(bookDto);
            return ResponseEntity.ok("Book data updated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating book: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload-cover/{bookId}")
    public ResponseEntity<String> uploadBookCover(@PathVariable long bookId,
                                                  @RequestParam("cover") MultipartFile coverFile) {
        try {
            bookService.updateBookCover(bookId, coverFile);
            return ResponseEntity.ok("Cover uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error uploading cover: " + e.getMessage());
        }
    }


    @GetMapping("/public")
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("booksCount", bookService.getAllBooksCount());
        model.addAttribute("reviewsCount", reviewService.getAllReviewsCount());
        model.addAttribute("authority", RolesUtil.getRole());
        return "books";
    }

    @GetMapping("/public/all-books")
    public ResponseEntity<String> getAllBooks() {
        List<BookDto> books = bookService.getAllBooks().stream()
                .map(BookDto::new)
                .toList();
        try {
            return ResponseEntity.ok(objectMapper.writeValueAsString(books));
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Error converting to JSON");
        }
    }

    @GetMapping("/public/search")
    public String searchBooksByTitle(@RequestParam(value = "title", required = false) String title, Model model) {
        if (title != null && !title.isEmpty()) {
            model.addAttribute("books", bookService.findBooksByTitleContaining(title));
        } else {
            model.addAttribute("books", bookService.getAllBooks());
        }

        model.addAttribute("booksCount", bookService.getAllBooksCount());
        model.addAttribute("reviewsCount", reviewService.getAllReviewsCount());
        model.addAttribute("authority", RolesUtil.getRole());
        return "books";
    }



    @GetMapping("/public/{id}")
    public String showBookDetails(@PathVariable long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("totalShelvesWithBook", bookService.countShelvesContainingBook(id));
        model.addAttribute("completedShelves", bookService.countShelvesContainingBookWithName(id, "Completed"));
        model.addAttribute("wantToReadShelves", bookService.countShelvesContainingBookWithName(id, "Want to Read"));


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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteBook(@RequestParam("bookId") Long bookId) {
        bookService.deleteBookById(bookId);
        return "redirect:/books/public";
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
