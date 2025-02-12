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


  public getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.apiUrl + "/books/public/all-books")
  }

}
