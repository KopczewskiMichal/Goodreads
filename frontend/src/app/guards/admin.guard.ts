import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';

export const ADMIN_GUARD: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn || !authService.isAdmin) {
    router.navigate(['/login']);

    return false;
  }

  return true;
};
