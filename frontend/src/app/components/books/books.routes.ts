import {Routes} from "@angular/router";
import { DetailsComponent } from "./details/details.component";
import { ADMIN_GUARD } from "../../guards/admin.guard";
import { BookFormComponent } from "./book-form/book-form.component";

export const BOOK_ROUTES: Routes = [
  {
    path: 'form',
    canActivate: [ADMIN_GUARD],
    component: BookFormComponent
  },
  {path: ':id', component: DetailsComponent},
  { path: '**', redirectTo: 'not-found'}
];
