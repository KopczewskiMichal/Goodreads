import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-add-review',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './add-review.component.html',
  styleUrl: './add-review.component.scss'
})
export class AddReviewComponent implements OnInit{
  public reviewsForm!: FormGroup;
  public fb = inject(FormBuilder);

  public ngOnInit(): void {
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
      console.log(this.reviewsForm.value);
    }
  }
}
