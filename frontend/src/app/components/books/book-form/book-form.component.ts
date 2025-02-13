import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { BookService } from './../../../services/book/book.service';
import { Book } from './../../../services/models/book.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-book-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.scss'],
  providers: [DatePipe]
})
export class BookFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private bookService = inject(BookService);
  private datePipe = inject(DatePipe);

  public bookForm!: FormGroup;
  public isEditMode = false;

  public defaultValues: Book = {
    bookId: 0,
    isbn: '',
    title: '',
    author: '',
    releaseDate: new Date(),
    description: '',
    pucharseLink: ''
  };

  public ngOnInit(): void {
    const selectedBook = this.bookService.getSelectedBook();
    this.isEditMode = !!selectedBook;

    this.bookForm = this.fb.group({
      isbn: [selectedBook?.isbn || this.defaultValues.isbn, Validators.required],
      title: [selectedBook?.title || this.defaultValues.title, Validators.required],
      author: [selectedBook?.author || this.defaultValues.author, Validators.required],
      releaseDate: [this.datePipe.transform(selectedBook?.releaseDate || this.defaultValues.releaseDate, 'yyyy-MM-dd'), Validators.required],
      description: [selectedBook?.description || this.defaultValues.description, Validators.required],
      pucharseLink: [selectedBook?.pucharseLink || this.defaultValues.pucharseLink]
    });
  }

  public onSubmit(): void {
    if (this.bookForm.valid) {
      const releaseDate = new Date(this.bookForm.value.releaseDate);
      const formattedReleaseDate = releaseDate.toISOString().replace("Z", "+0000");
      const bookData: Book = {
        bookId: this.isEditMode ? this.bookService.getSelectedBook()!.bookId : 0,
        ...this.bookForm.value,
        releaseDate: formattedReleaseDate
      };
  
      if (this.isEditMode) {
        this.bookService.updateBook(bookData).subscribe({
          next: () => console.log('Książka zaktualizowana pomyślnie'),
          error: (err) => console.error('Błąd podczas aktualizacji książki:', err)
        });
      } else {
        this.bookService.addBook(bookData).subscribe({
          next: () => console.log('Książka dodana pomyślnie'),
          error: (err) => console.error('Błąd podczas dodawania książki:', err)
        });
      }
    }
  }
  
}