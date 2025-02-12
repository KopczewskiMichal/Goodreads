import { Component, inject, OnInit } from '@angular/core';
import { Book } from '../../../services/models/book.model';
import { BookService } from '../../../services/book/book.service';
import { Router, ActivatedRoute } from '@angular/router';
import { BookComponent } from "../book/book.component";
import { Review } from '../../../services/models/review.model';
import { ReviewComponent } from '../review/review.component';
import { AddReviewComponent } from '../review/add-review/add-review.component';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [BookComponent, ReviewComponent, AddReviewComponent],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss'
})
export class DetailsComponent implements OnInit {
  public book: Book | null = null;
  public reviews: Review[] = [];
  private bookService = inject(BookService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  public ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') as string;
    if (id) {
      this.loadBookAndReviews(+id);
    } else {
      this.router.navigate(['not-found']);
    }
  }

  private loadBookAndReviews(id: number): void {
    this.bookService.getSingleBook(id).subscribe({
      next: (book: Book) => {
        this.book = book;
        this.loadReviews(id);
      },
      error: () => this.router.navigate(['not-found'])
    });
  }

  private loadReviews(id: number): void {
    this.bookService.getBookReviews(id).subscribe({
      next: (reviews: Review[]) => {
        this.reviews = reviews;
      },
      error: () => this.router.navigate(['not-found'])
    });
  }
}
