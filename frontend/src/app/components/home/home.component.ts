import { Component, OnInit, inject } from '@angular/core';
import { Book } from '../../services/models/book.model';
import { BookService } from '../../services/book/book.service';
import { BookComponent } from '../books/book/book.component';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [BookComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit{
  public books: Book[] = [];
  private bookService = inject(BookService);
  public errorMessage: string = '';



  public ngOnInit(): void {
    this.loadBooks();
  }

  private loadBooks(): void {
    this.bookService.getAllBooks().subscribe({
      next: (books: Book[]) => {
        this.books = books; 
      },
      error: (err) => {
        this.errorMessage = 'Nie udało się pobrać książek';
        console.error('Błąd pobierania książek:', err);
      }
    });
  }
}
