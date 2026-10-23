import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  form: FormGroup;
  loading = false;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private auth: AuthService,
    private toast: ToastService,
    private router: Router,
  ) {
    if (this.auth.isLoggedIn()) {
      const alPago = sessionStorage.getItem('raymi_pagar_despues') === '1';
      sessionStorage.removeItem('raymi_pagar_despues');
      this.router.navigate([alPago ? '/checkout' : '/']);
    }

    this.form = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName:  ['', [Validators.required, Validators.minLength(2)]],
      username:  ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-zA-Z0-9._-]+$/)]],
      email:     ['', [Validators.required, Validators.email]],
      pais:      ['Perú', Validators.required],
      ciudad:    ['', Validators.required],
      direccionEnvio: ['', [Validators.required, Validators.minLength(5)]],
      password:  ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(group: AbstractControl) {
    const pw  = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    return pw === cpw ? null : { mismatch: true };
  }

  submit(): void {
      console.log("ENTRO AL SUBMIT");
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
      console.log("VOY A ENVIAR EL POST");
    this.loading = true;

    const { firstName, lastName, username, email, password, pais, ciudad, direccionEnvio } = this.form.value;

    // El endpoint de registro es SUPER_ADMIN, pero para auto-registro de clientes
    // puede estar abierto o usar un endpoint público especial.
    // Usamos ms-auth con rol CLIENTE (sin token = registro público)
    this.http.post(`${environment.apiUrl}/api/v1/auth`, {
      username, email, firstName, lastName,
      password,
      roles: ['CLIENTE'],
      enabled: true,
    }).subscribe({
      next: () => {
        this.loading = false;
        sessionStorage.setItem('raymi_envio_registro', JSON.stringify({ pais, ciudad, direccionEnvio }));
        this.toast.show('Cuenta creada. Ahora entra con Keycloak.');
        void this.auth.iniciarSesion(this.auth.destinoDespuesDeEntrar());
      },
      error: (err) => {
        this.loading = false;
        const raw = JSON.stringify(err.error || '');
        const msg = err.status === 409 || raw.includes('same email') || raw.includes('same username')
          ? 'Ese correo o usuario ya tiene una cuenta. Inicia sesión para continuar.'
          : (err.error?.message || 'No se pudo completar el registro. Inténtalo nuevamente.');
        this.toast.show(msg, 'error');
      },
    });
  }

  hasError(field: string, error: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl && ctrl.touched && ctrl.hasError(error));
  }

  get passwordMismatch(): boolean {
    return !!(this.form.get('confirmPassword')?.touched && this.form.hasError('mismatch'));
  }
}
