import { Component, inject, Input } from '@angular/core';
import { Review } from '../../../services/models/review.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-review',
  standalone: true,
  providers: [DatePipe],
  imports: [],
  templateUrl: './review.component.html',
  styleUrl: './review.component.scss'
})
export class ReviewComponent {
  @Input() public review!: Review;
  public datePipe = inject(DatePipe);

  public formatDate(date: Date): string | null {
    return this.datePipe.transform(date, 'dd.MM.yyyy HH:mm');
  }
}
