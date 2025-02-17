import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Book } from '../models/book.model';

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

  public setSelectedBook(book: Book | null): void {
    this.selectedBook = book;
  }

  public getSelectedBook(): Book | null {
    return this.selectedBook;
  }

  public addBook(book: Book): Observable<Book> {
    return this.http.post<Book>(this.apiUrl + "/books/api/add", book, {withCredentials: true, responseType: 'text' as 'json'});
  }

  public updateBook(book: Book): Observable<string> {
    return this.http.post<string>(this.apiUrl + "/books/api/edit", book, {withCredentials: true, responseType: 'text' as 'json'});
  }

  public deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(this.apiUrl + "/books/api/delete/" + id, {withCredentials: true});
  }

  public reloadSelectedBook(): void {
    if (!this.selectedBook) {
      return;
    }
  
    this.getSingleBook(this.selectedBook.bookId).subscribe({
      next: (book) => this.selectedBook = book,
      error: (err) => console.error("Failed to reload book:", err)
    });
  }
  
}
