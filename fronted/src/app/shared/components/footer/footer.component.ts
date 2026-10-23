import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class FooterComponent {
  year = new Date().getFullYear();

  constructor(private auth: AuthService) {}

  crearCuenta(): void {
    this.auth.olvidarQuieroPagar();
  }
}
