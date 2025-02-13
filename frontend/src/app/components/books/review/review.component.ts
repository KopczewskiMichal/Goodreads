import { Component, inject, Input, Output, EventEmitter } from '@angular/core';
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
  @Output() private delete = new EventEmitter<void>();
  public datePipe = inject(DatePipe);

  public formatDate(date: Date): string | null {
    return this.datePipe.transform(date, 'dd.MM.yyyy HH:mm');
  }

  public deleteReview(): void {
    this.delete.emit();
  }
}
