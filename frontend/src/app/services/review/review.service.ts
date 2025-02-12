import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from './../../../environments/environment';
import { Review } from '../models/review.model';
import { Observable, forkJoin } from 'rxjs';
import { catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private http = inject(HttpClient);
  private apiUrl: string = environment.apiUrl;

  public getBookReviews(id: number): Observable<Review[]> {
    return this.http.get<Review[]>(this.apiUrl + "/review/public/get-by-bookId/" + id);
  }
  
  public addSingleReview(bookId: number, review: { reviewText: string; stars: number }): Observable<string> {
    const params = new HttpParams()
      .set('reviewText', review.reviewText)
      .set('stars', review.stars.toString());

    return this.http.post(`${this.apiUrl}/review/add/${bookId}`, null, {
      responseType: 'text',
      withCredentials: true,
      params: params,
    }).pipe(
      catchError((error) => {
        console.error('Error adding review:', error);
        throw error;
      })
    );
  }

  public addMultipleReviews(bookId: number, reviews: { reviewText: string; stars: number }[]): Observable<string[]> {
    const reviewObservables = reviews.map((review) => this.addSingleReview(bookId, review));
    
    return forkJoin(reviewObservables);
  }
  
}
