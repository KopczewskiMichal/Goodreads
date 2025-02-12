import {Routes} from "@angular/router";
import { DetailsComponent } from "./details/details.component";

export const BOOK_ROUTES: Routes = [
  {path: ':id', component: DetailsComponent},
  { path: '**', redirectTo: 'not-found'},
];
