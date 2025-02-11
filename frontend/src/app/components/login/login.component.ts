import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule, FormBuilder, Validators} from '@angular/forms';
import { environment } from './../../../environments/environment';
import { AuthService } from '../../services/auth/auth.service';

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
        next: () => this.message = "Zalogowano pomyślnie!",
        error: (error) => this.message = `Błąd logowania: ${error.message}`
      });
    } else {
      this.message = "Please fill all fields";
    }
  }
}
