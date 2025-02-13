import { CommonModule, Location} from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormGroup, NonNullableFormBuilder, Validators, ReactiveFormsModule, FormControl } from '@angular/forms';
import { BookService } from './../../../services/book/book.service';
import { Book } from './../../../services/models/book.model';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { pastDateValidator } from '../../../validators/past-date.validator';
import { Router } from '@angular/router';


interface BookForm {
  isbn: FormControl<string | null>
  title: FormControl<string | null>
  author: FormControl<string | null >
  releaseDate: FormControl<Date | null>
  description: FormControl<string | null>
  pucharseLink: FormControl<string | null>
};

@Component({
  selector: 'app-book-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.scss'],
  providers: [DatePipe]
})
export class BookFormComponent implements OnInit {
  private fb = inject(NonNullableFormBuilder);
  private bookService = inject(BookService);
  private location = inject(Location);
  private router = inject(Router);

  public bookForm!: FormGroup<BookForm>;
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
    this.initializeForm(selectedBook);
  }

  // eslint-disable-next-line complexity
  private initializeForm(selectedBook: Book | null): void {
    this.bookForm = this.fb.group<BookForm>({
      isbn: new FormControl<string | null>(selectedBook?.isbn || this.defaultValues.isbn, {
        validators: [Validators.required, Validators.minLength(10), Validators.maxLength(13)]
      }),
      title:  new FormControl<string | null>(selectedBook?.title || this.defaultValues.title, {
        validators: [Validators.required]
      }),
      author:  new FormControl<string | null>(selectedBook?.author || this.defaultValues.author, {
        validators: [Validators.required]
      }),
      releaseDate:  new FormControl<Date | null>(selectedBook?.releaseDate || this.defaultValues.releaseDate, {
        validators: [Validators.required, pastDateValidator]
      }),
      
      description: new FormControl<string | null>(this.defaultValues.title, {
        validators: [Validators.required, Validators.maxLength(255)]
      }),
      pucharseLink:  new FormControl<string | null>(this.defaultValues.title, {
        validators: []
      }),
    });
  }

  public onSubmit(): void {
    if (this.bookForm.valid) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      const bookData: Book = {
        bookId: this.isEditMode ? this.bookService.getSelectedBook()!.bookId : 0,
        ...this.bookForm.value, releaseDate: new Date(this.bookForm.value.releaseDate!)
      } as Book;

      if (this.isEditMode) {
        this.bookService.updateBook(bookData).subscribe({
          next: () => {
            this.bookService.reloadSelectedBook();
            this.location.back();
          },
          error: (err: HttpErrorResponse) => console.error('Error updating book:', err)
        });
      } else {
        this.bookService.addBook(bookData).subscribe({
          next: () => {
            this.router.navigate(['home']);
          },
          error: (err: HttpErrorResponse) => console.error('Error adding book:', err)
        });
      }
    }
  }
}