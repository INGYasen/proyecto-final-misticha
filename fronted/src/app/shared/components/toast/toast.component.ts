import { Component, OnInit } from '@angular/core';
import { ToastService, Toast } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  template: `
    <div class="toast-container">
      <div
        class="toast"
        [class.toast--error]="t.type === 'error'"
        [class.toast--info]="t.type === 'info'"
        *ngFor="let t of toasts"
        (click)="toast.remove(t.id)"
      >
        {{ t.message }}
      </div>
    </div>
  `,
  styles: [`
    .toast--error { border-left-color: #e74c3c !important; }
    .toast--info  { border-left-color: #3498db !important; }
    .toast { cursor: pointer; }
  `],
})
export class ToastComponent implements OnInit {
  toasts: Toast[] = [];
  constructor(public toast: ToastService) {}
  ngOnInit(): void { this.toast.toasts$.subscribe(t => this.toasts = t); }
}
