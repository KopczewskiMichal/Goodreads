import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AUTH_GUARD, AUTH_GUARD_CHILD } from './guards/auth-guard.guard';


export const routes: Routes = [
  { path: "home",
    component: HomeComponent
  },
  {
    path: "login",
    component: LoginComponent
  },
  {
    path: "user",
    loadChildren: () => import('./components/user/user.routes').then((r)=> r.USER_ROUTES),
    canActivate: [AUTH_GUARD],
    canActivateChild: [AUTH_GUARD_CHILD]
  },
  {
    path: '**',
    redirectTo: 'home',
  },
//   { path: '**', redirectTo: 'not-found' }
];
