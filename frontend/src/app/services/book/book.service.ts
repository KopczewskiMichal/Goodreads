import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Book } from '../models/book.model';
import { Review } from '../models/review.model';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  private selectedBook: Book | null = null;


  public getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.apiUrl + "/books/public/all-books");
  }

  public getSingleBook(id: number): Observable<Book> {
    return this.http.get<Book>(this.apiUrl + "/books/public/get-as-dto/" + id);
  }

  public getBookReviews(id: number): Observable<Review[]> {
    return this.http.get<Review[]>(this.apiUrl + "/review/public/get-by-bookId/" + id);
  }

  public setSelectedBook(book: Book): void {
    this.selectedBook = book;
  }

  public getSelectedBook(): Book | null {
    return this.selectedBook;
  }

}
