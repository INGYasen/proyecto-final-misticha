import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css'],
})
export class ContactComponent {
  readonly phoneDisplay = '916 367 161';
  readonly phoneTel = '+51916367161';
  readonly whatsapp = 'https://wa.me/51916367161';
  readonly email = 'hola@raymisupreme.pe';
  sent = false;

  readonly form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    correo: ['', [Validators.required, Validators.email]],
    mensaje: ['', [Validators.required, Validators.minLength(8)]],
  });

  constructor(private fb: FormBuilder) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const nombre = this.form.controls.nombre.value?.trim();
    const correo = this.form.controls.correo.value?.trim();
    const mensaje = this.form.controls.mensaje.value?.trim();
    const text = `Hola, soy ${nombre} (${correo}).\n${mensaje}`;
    const opened = window.open(`${this.whatsapp}?text=${encodeURIComponent(text)}`, '_blank', 'noopener');
    this.sent = true;
    if (!opened) {
      window.location.href = `${this.whatsapp}?text=${encodeURIComponent(text)}`;
    }
  }

  invalid(field: 'nombre' | 'correo' | 'mensaje'): boolean {
    const control = this.form.controls[field];
    return control.touched && control.invalid;
  }
}
