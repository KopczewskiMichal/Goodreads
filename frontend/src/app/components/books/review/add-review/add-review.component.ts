import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, Input} from '@angular/core';
import { FormGroup, FormBuilder, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { ReviewService } from '../../../../services/review/review.service';
import { ActivatedRoute } from '@angular/router';



@Component({
  selector: 'app-add-review',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './add-review.component.html',
  styleUrl: './add-review.component.scss'
})
export class AddReviewComponent implements OnInit{
  @Input() public bookId!: number;
  public reviewsForm!: FormGroup;
  public fb = inject(FormBuilder);
  private reviewService = inject(ReviewService);
  private route = inject(ActivatedRoute);

  public ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.bookId = Number(params.get('id')); // Rzutowanie na liczbę
      console.log('Book ID:', this.bookId);
    });
    this.reviewsForm = this.fb.group({
      reviews: this.fb.array([this.createReview()])
    });
  }


  public createReview(): FormGroup {
    return this.fb.group({
      reviewText: ['', Validators.required], 
      stars: [3, [Validators.required, Validators.min(1), Validators.max(5)]]
    });
  }

  public addReview(): void {
    const reviews = this.reviewsForm.get('reviews') as FormArray;
    reviews.push(this.createReview());
  }

  public removeReview(index: number): void {
    const reviews = this.reviewsForm.get('reviews') as FormArray;
    reviews.removeAt(index);
  }

  public get reviews(): FormArray {
    return this.reviewsForm.get('reviews') as FormArray;
  }

  public onSubmit(): void {
    if (this.reviewsForm.valid) {
      console.log(this.reviewsForm.value.reviews);
      this.reviewService.addMultipleReviews(this.bookId, this.reviewsForm.value.reviews)
        .subscribe({
          next: () => {
            console.log('Review Added');
            this.reviewsForm.reset();
          },
          error: (err) => console.error('Błąd dodawania recenzji:', err)
        });
    }
  }
}
