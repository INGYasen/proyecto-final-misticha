import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { StripeSessionResponse } from '../../../core/models/models';
import { retry, throwError, timer } from 'rxjs';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-payment-result',
  template: `
    <main class="payment-result">
      <h1>{{ title }}</h1><p>{{ message }}</p>
      <div class="details" *ngIf="stripeSession">
        <p><strong>Sesión:</strong> {{ stripeSession.sessionId }}</p>
        <p><strong>Pago:</strong> {{ stripeSession.paymentStatus }}</p>
        <p><strong>Estado:</strong> {{ stripeSession.status }}</p>
        <p><strong>Monto:</strong> {{ stripeSession.amountTotal / 100 | number:'1.2-2' }} {{ stripeSession.currency | uppercase }}</p>
      </div>
      <a class="voucher-link" *ngIf="stripeSession?.paymentStatus === 'paid'"
         [routerLink]="['/voucher', stripeSession?.ventaId]">Ver mi boleta</a>
      <button type="button" *ngIf="verificationFailed" (click)="verifyPayment()">Reintentar verificación</button>
      <a routerLink="/mis-pedidos">Ver mis pedidos</a>
    </main>`,
  styles: [`.payment-result{max-width:680px;margin:5rem auto;padding:2rem;text-align:center}.details{margin:2rem 0;padding:1rem;background:#f7f7f7;border-radius:12px}a,button{display:inline-block;margin:1rem .5rem}.voucher-link,button{padding:.75rem 1.25rem;cursor:pointer}.voucher-link{background:#c9922a;color:#fff;text-decoration:none;border-radius:6px}`],
})
export class PaymentResultComponent implements OnInit {
  title: string;
  message: string;
  stripeSession?: StripeSessionResponse;
  verificationFailed = false;
  private sessionId: string | null = null;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private order: OrderService,
    private cart: CartService,
  ) {
    this.title = route.snapshot.data['title'] || 'Resultado del pago';
    this.message = route.snapshot.data['message'] || '';
  }
  ngOnInit(): void {
    this.sessionId = this.route.snapshot.queryParamMap.get('session_id');
    if (!this.sessionId) return;
    this.verifyPayment();
  }

  verifyPayment(): void {
    if (!this.sessionId) return;
    this.verificationFailed = false;
    this.title = 'Verificando pago con Stripe…';
    this.message = 'Estamos confirmando el pago de forma segura.';
    this.order.consultarStripeSession(this.sessionId).pipe(
      retry({
        count: 4,
        delay: error => error.status === 503
          ? timer(1500)
          : throwError(() => error),
      }),
    ).subscribe({
      next: session => {
        this.stripeSession = session;
        if (session.paymentStatus === 'paid') {
          this.prepareVoucher(session);
          this.router.navigate(['/voucher', session.ventaId], { replaceUrl: true });
          return;
        }
        this.title = session.paymentStatus === 'paid' ? 'Pago aprobado' : 'Pago pendiente';
        this.message = session.paymentStatus === 'paid'
          ? 'Stripe confirmó el pago y el pedido fue actualizado.'
          : 'Stripe todavía no registra la sesión como pagada.';
      },
      error: error => {
        this.verificationFailed = true;
        this.title = 'No se pudo verificar el pago';
        this.message = error.status === 503
          ? 'El servicio de pagos está iniciando. Tu pago no se perdió; pulsa Reintentar verificación.'
          : (error.error?.message || error.message);
      },
    });
  }

  private prepareVoucher(session: StripeSessionResponse): void {
    const cliente = this.order.getCachedCliente();
    const items = this.cart.items.map(item => ({ ...item }));
    const voucher = {
      idPedido: Number(session.ventaId),
      fecha: new Date().toISOString(),
      nombreCliente: cliente ? `${cliente.nombres} ${cliente.apellidos || ''}`.trim() : 'Cliente',
      email: cliente?.email || '',
      pais: cliente?.pais || 'Perú',
      ciudad: cliente?.ciudad || '',
      direccion: cliente?.direccionEnvio || '',
      metodoPago: 'STRIPE',
      items,
      total: session.amountTotal / 100,
      stripeSessionId: session.sessionId,
    };
    sessionStorage.setItem('raymisupreme_last_voucher', JSON.stringify(voucher));
    // El pago ya fue confirmado por Stripe. Conservamos primero los artículos
    // en la boleta y luego limpiamos el carrito para evitar una compra duplicada.
    this.cart.clearCart();
  }
}
