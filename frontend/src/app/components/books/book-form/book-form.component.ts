import { CommonModule, Location} from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { BookService } from './../../../services/book/book.service';
import { Book } from './../../../services/models/book.model';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { pastDateValidator } from '../../../validators/past-date.validator';

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
  private location = inject(Location);

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

    this.initializeForm(selectedBook);
  }

  // eslint-disable-next-line complexity
  private initializeForm(selectedBook: Book | null): void {
    this.bookForm = this.fb.group({
      isbn: [selectedBook?.isbn || this.defaultValues.isbn, Validators.required],
      title: [selectedBook?.title || this.defaultValues.title, Validators.required],
      author: [selectedBook?.author || this.defaultValues.author, Validators.required],
      releaseDate: [
        this.datePipe.transform(selectedBook?.releaseDate || this.defaultValues.releaseDate, 'yyyy-MM-dd'),
        [Validators.required, pastDateValidator()]
      ],
      description: [
        selectedBook?.description || this.defaultValues.description,
        [Validators.maxLength(255)]
      ],
      pucharseLink: [selectedBook?.pucharseLink || this.defaultValues.pucharseLink]
    });
  }

  public onSubmit(): void {
    if (this.bookForm.valid) {
      const releaseDate = new Date(this.bookForm.value.releaseDate);
      const formattedReleaseDate = releaseDate.toISOString().replace("Z", "+0000");
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      const bookData: Book = {
        bookId: this.isEditMode ? this.bookService.getSelectedBook()!.bookId : 0,
        ...this.bookForm.value,
        releaseDate: formattedReleaseDate
      };

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
          error: (err: HttpErrorResponse) => console.error('Error adding book:', err)
        });
        this.location.back();
      }
    }
  }
}