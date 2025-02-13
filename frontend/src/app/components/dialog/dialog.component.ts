import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-dialog',
  standalone: true,
  imports: [],
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.scss'
})
export class DialogComponent {
  @Output() private confirm = new EventEmitter<boolean>(); 
  public onCancel(): void {
    this.confirm.emit(false); 
  }

  public onConfirm(): void {
    this.confirm.emit(true); 
  }
}
