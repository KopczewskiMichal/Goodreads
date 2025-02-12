import { Component, inject, OnInit } from '@angular/core';
import { Book } from '../../../services/models/book.model';
import { BookService } from '../../../services/book/book.service';
import { Router, ActivatedRoute } from '@angular/router';
import { BookComponent } from "../book/book.component";

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [BookComponent],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss'
})
export class DetailsComponent implements OnInit {
  public book: Book | null = null;
  private bookService = inject(BookService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  public ngOnInit(): void {
    this.book = this.bookService.getSelectedBook();

    if (!this.book) {
      const id = this.route.snapshot.paramMap.get('id') as string;
      if (id) {
        this.book = this.bookService.getSelectedBook();
        // eslint-disable-next-line max-depth
        if (!this.book) {
          this.bookService.getSingleBook(+id).subscribe({
            next: (book: Book) => this.book = book,
            error: () => this.router.navigate(['not-found'])
          });
        }
      } else {
        this.router.navigate(['not-found']);
      }

    }
  }
}
