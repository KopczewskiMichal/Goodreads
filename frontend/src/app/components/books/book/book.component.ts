import { Component, Input } from '@angular/core';
import { Book } from '../../../services/models/book.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-book',
  standalone: true,
  imports: [],
  templateUrl: './book.component.html',
  styleUrl: './book.component.scss'
})
export class BookComponent {
  @Input() public book!: Book;
  public apiUrl = environment.apiUrl;
}
