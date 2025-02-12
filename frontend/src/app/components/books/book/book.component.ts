import { Component, Input } from '@angular/core';
import { Book } from '../../../services/models/book.model';
import { environment } from '../../../../environments/environment';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-book',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './book.component.html',
  styleUrl: './book.component.scss'
})
export class BookComponent {
  @Input() public book!: Book;
  public apiUrl = environment.apiUrl;
}
