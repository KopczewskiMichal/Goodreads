import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, Input, Output, EventEmitter} from '@angular/core';
import { FormGroup, NonNullableFormBuilder, FormArray, Validators, ReactiveFormsModule, FormControl } from '@angular/forms';
import { ReviewService } from '../../../../services/review/review.service';
import { ActivatedRoute } from '@angular/router';

interface SingleReviewForm {
  reviewText: FormControl<string | null>;
  stars: FormControl<number | null>;
}

interface ReviewsForm {
  reviews: FormArray<FormGroup<SingleReviewForm>>;
}

@Component({
  selector: 'app-add-review',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './add-review.component.html',
  styleUrl: './add-review.component.scss'
})
export class AddReviewComponent implements OnInit{
  @Input() public bookId!: number;
  public reviewsForm!: FormGroup<ReviewsForm>;
  public fb = inject(NonNullableFormBuilder);
  private reviewService = inject(ReviewService);
  private route = inject(ActivatedRoute);

  @Output() private reviewAdded = new EventEmitter<void>();

  private defaultValues = {
    reviewText: '',
    stars: 3
  };

  public ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.bookId = Number(params.get('id'));
    });
    this.reviewsForm = this.fb.group<ReviewsForm>({
      reviews: this.fb.array<FormGroup<SingleReviewForm>>([this.createReview()])
    });
  }


  public createReview(): FormGroup {
    return this.fb.group<SingleReviewForm>({
      reviewText: new FormControl<string | null>(this.defaultValues.reviewText, {validators: [Validators.required]}), 
      stars: new FormControl<number | null>(this.defaultValues.stars, {validators:[Validators.required, Validators.min(1), Validators.max(5)]})
    });
  }

  public addReview(): void {
    const reviews = this.reviewsForm.get('reviews') as FormArray<FormGroup<SingleReviewForm>>;
    reviews.push(this.createReview());
  }

  public removeReview(index: number): void {
    const reviews = this.reviewsForm.get('reviews') as FormArray<FormGroup<SingleReviewForm>>;
    reviews.removeAt(index);
  }

  public get reviews(): FormArray {
    return this.reviewsForm.get('reviews') as FormArray<FormGroup<SingleReviewForm>>;
  }

  public onSubmit(): void {
    if (this.reviewsForm.valid) {
      this.reviewService.addMultipleReviews(this.bookId, this.reviews.value)
        .subscribe({
          next: () => {
            this.reviewsForm.setControl('reviews', this.fb.array([this.createReview()]));
            this.reviewsForm.reset({
              reviews: [this.defaultValues]
            });
            this.reviewAdded.emit();
          },
          error: (err) => console.error('Błąd dodawania recenzji:', err)
        });
    }
  }
}
