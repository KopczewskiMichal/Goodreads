import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './../../services/auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  public authService = inject(AuthService);
  private router = inject(Router);

  public navigateToHome(): void {
    this.router.navigate(['home']);
  }
  public navigateToLogin(): void {
    this.router.navigate(['login']);
  }

  public logout(): void {
    this.authService.logout();
    this.router.navigate(['login']);
  }

  public goToAddBook(): void {
    if (!this.authService.isAdmin) {
      this.router.navigate(['login']);
    } else {
      this.router.navigate(['book', 'form']);
    }
  }
}
