import { Component, inject, OnInit } from '@angular/core';
import { Book } from '../../../services/models/book.model';
import { BookService } from '../../../services/book/book.service';
import { Router, ActivatedRoute } from '@angular/router';
import { BookComponent } from "../book/book.component";
import { Review } from '../../../services/models/review.model';
import { ReviewComponent } from '../review/review.component';
import { AddReviewComponent } from '../review/add-review/add-review.component';
import { AuthService } from '../../../services/auth/auth.service';
import { ReviewService } from '../../../services/review/review.service';
import { DialogComponent } from '../../dialog/dialog.component';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [BookComponent, ReviewComponent, AddReviewComponent, DialogComponent],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss'
})
export class DetailsComponent implements OnInit {
  public book: Book | null = null;
  public reviews: Review[] = [];
  private bookService = inject(BookService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  public authService = inject(AuthService);
  private reviewService = inject(ReviewService);
  public isCommentDialogOpen: boolean = false;
  public isBookDialogOpen: boolean = false;
  public selectedReviewId: number | null = null;

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
        this.bookService.setSelectedBook(book);
        this.loadReviews(id);
      },
      error: () => this.router.navigate(['not-found'])
    });
  }

  public loadReviews(id: number): void {
    this.reviewService.getBookReviews(id).subscribe({
      next: (reviews: Review[]) => {
        this.reviews = reviews;
      },
      error: () => this.router.navigate(['not-found'])
    });
  }

  public goToEditBook(): void {
    if (!this.authService.isAdmin) {
      this.router.navigate(['login']);
    } else {
      this.router.navigate(['book', 'form']);
    }
  }

  public deleteBook(): void {
    if (!this.authService.isAdmin) {
      this.router.navigate(['login']);
    } else {
      this.bookService.deleteBook(this.book!.bookId).subscribe({
        next: () => {
          this.router.navigate(['home']);
        },
        error: () => this.router.navigate(['home'])
      });
    }
  }

  public openCommentDialog(reviewId: number): void {
    this.selectedReviewId = reviewId;
    this.isCommentDialogOpen = true;
  }
  public openBookDialog(): void {
    this.isBookDialogOpen = true;
  }

  public handleConfirmDeleteComment(confirmed: boolean): void {
    this.isCommentDialogOpen = false; 
    if (confirmed && this.selectedReviewId !== null) {
      this.deleteReview(this.selectedReviewId); 
    }
    this.selectedReviewId = null; 
  }

  public handleConfirmDeleteBook(confirmed: boolean): void {
    this.isBookDialogOpen = false; 
    if (confirmed ) {
      this.deleteBook();
    }
  }


  public deleteReview(id: number): void {
    if (!this.authService.isAdmin) {
      this.router.navigate(['login']);
    } else {
      this.reviewService.deleteReview(id).subscribe({
        next: () => {
          this.loadReviews(this.book!.bookId);
        },
        error: () => console.error("Review doesn't exitsts"),
      });
    }
  }
}
