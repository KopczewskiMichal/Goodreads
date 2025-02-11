import { inject } from '@angular/core';
import { CanActivateFn, CanActivateChildFn, Router } from '@angular/router';
import { AuthService } from './../services/auth/auth.service';

export const AUTH_GUARD: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn) {
    router.navigate(['/login']);

    return false;
  }

  return true;
};

export const AUTH_GUARD_CHILD: CanActivateChildFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn) {
    router.navigate(['/login']);

    return false;
  }

  return true;
};
