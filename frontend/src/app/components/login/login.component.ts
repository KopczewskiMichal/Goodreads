import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule, FormBuilder, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from './../../../environments/environment';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})

export class LoginComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  public loginForm!: FormGroup;
  public message = "";
  public apiUrl: string = environment.apiUrl;
  private authService = inject(AuthService);
  public ngOnInit(): void {
    this.loginForm = this.formBuilder.nonNullable.group({
      identifier: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  public onSubmit(): void {
    if (this.loginForm.valid) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      const { identifier, password} = this.loginForm.value;

      this.authService.login(identifier, password).subscribe({
        next: () => this.router.navigate(['user']),
        error: (error) => this.message = `Błąd logowania: ${error.message}`
      });
    } else {
      this.message = "Please fill all fields";
    }
  }
}
