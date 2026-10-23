import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loading = false;
  error = '';

  constructor(
    private auth: AuthService,
    private toast: ToastService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    if (this.auth.isLoggedIn() && !window.location.search.includes('code=')) {
      this.redirigir();
      return;
    }
    this.auth.completarSiVuelve().subscribe({
      next: entro => {
        if (!entro) return;
        this.toast.show('Bienvenida/o a Raymi Suprême');
        this.redirigir();
      },
      error: () => {
        this.error = 'El regreso de Keycloak no coincidió. Vuelve a entrar.';
        this.toast.show(this.error, 'error');
      },
    });
  }

  entrar(): void {
    this.loading = true;
    void this.auth.iniciarSesion(this.auth.destinoDespuesDeEntrar());
  }

  seguirMirando(): void {
    this.auth.olvidarQuieroPagar();
  }

  private redirigir(): void {
    const user = this.auth.currentUser;
    const roles: string[] = user?.realm_access?.roles ?? [];
    const pagar = sessionStorage.getItem('raymi_pagar_despues') === '1';
    sessionStorage.removeItem('raymi_pagar_despues');
    if (roles.includes('SUPER_ADMIN') || roles.includes('ADMIN')) {
      this.router.navigate(['/admin']);
    } else if (roles.includes('VENDEDORA')) {
      this.router.navigate(['/vendedora']);
    } else if (pagar) {
      this.router.navigate(['/checkout']);
    }
  }
}
