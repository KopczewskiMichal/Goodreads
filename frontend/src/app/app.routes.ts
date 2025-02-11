import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';


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
    loadChildren: () => import('./components/user/user.routes').then((r)=> r.USER_ROUTES)
  },
  {
    path: '**',
    redirectTo: 'home',
  },
//   {
//     path: "user",
//     component: ProfileComponent,
//   },
//   { path: '**', redirectTo: 'not-found' }
];
