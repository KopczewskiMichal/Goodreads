import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule, FormBuilder, Validators} from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private formBuilder = inject(FormBuilder);
  public loginForm!: FormGroup;
  public message = "";
  public ngOnInit(): void {
    this.loginForm = this.formBuilder.nonNullable.group({
      identifier: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  public onSubmit(): void {
    if (this.loginForm.valid) {
      console.log(this.loginForm.value);
    } else {
      this.message = "Please fill all fields";
    }
  }
}
