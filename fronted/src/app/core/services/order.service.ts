import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';
import {
  Cliente, Pedido, DetallePedido, PagoRequest, MetodoPago, MercadoPagoPreferenciaResponse,
  MercadoPagoPaymentRequest, MercadoPagoPaymentResponse
  , StripeCheckoutResponse, StripeSessionResponse
} from '../models/models';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient, private auth: AuthService) {}

  private get clienteCacheKey(): string {
    return `raymisupreme_cliente_${this.auth.currentUser?.sub || 'anonimo'}`;
  }

  // ── Clientes ──────────────────────────────────────────────────

  createCliente(cliente: Omit<Cliente, 'idCliente'>): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.base}/api/v1/clientes`, cliente);
  }

  getCliente(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.base}/api/v1/clientes/${id}`);
  }

  getCurrentCliente(): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.base}/api/v1/clientes/me`);
  }

  cacheCurrentCliente(cliente: Cliente): void {
    localStorage.setItem(this.clienteCacheKey, JSON.stringify(cliente));
  }

  getCachedCliente(): Cliente | null {
    try {
      const raw = localStorage.getItem(this.clienteCacheKey);
      return raw ? JSON.parse(raw) as Cliente : null;
    } catch {
      return null;
    }
  }

  // ── Pedidos ───────────────────────────────────────────────────

  createPedido(pedido: Omit<Pedido, 'idPedido'>): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.base}/api/v1/pedidos`, pedido);
  }

  getPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.base}/api/v1/pedidos`);
  }

  getMisPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.base}/api/v1/pedidos/mios`);
  }

  getDetallesPedido(idPedido: number): Observable<DetallePedido[]> {
    return this.http.get<DetallePedido[]>(`${this.base}/api/v1/detalles`, { params: { idPedido } });
  }

  getPedidosByCliente(idCliente: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.base}/api/v1/pedidos?idCliente=${idCliente}`);
  }

  getPedido(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.base}/api/v1/pedidos/${id}`);
  }

  confirmarPedido(idPedido: number, metodoPago: MetodoPago): Observable<any> {
    return this.http.post(`${this.base}/api/v1/pedidos/${idPedido}/confirmar`, { metodoPago });
  }

  cancelarPedido(idPedido: number): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.base}/api/v1/pedidos/${idPedido}/cancelar`, {});
  }

  // ── Detalles ──────────────────────────────────────────────────

  addDetalle(detalle: Omit<DetallePedido, 'idDetalle'>): Observable<DetallePedido> {
    return this.http.post<DetallePedido>(`${this.base}/api/v1/detalles`, detalle);
  }

  // ── Checkout completo ─────────────────────────────────────────

  checkout(idCliente: number, metodoPago?: MetodoPago): Observable<any> {
    return this.http.post(`${this.base}/api/v1/ventas/checkout/${idCliente}`,
      metodoPago ? { metodoPago } : {}
    );
  }

  // ── Pagos ─────────────────────────────────────────────────────

  procesarPago(pago: PagoRequest): Observable<any> {
    return this.http.post(`${this.base}/api/pagos/procesar`, pago);
  }

  crearPreferenciaMercadoPago(pedidoId: number): Observable<MercadoPagoPreferenciaResponse> {
    return this.http.post<MercadoPagoPreferenciaResponse>(
      `${this.base}/api/v1/pagos/mercadopago/preferencia`,
      { pedidoId }
    );
  }

  pagoDePedido(pedidoId: number): Observable<{ estado?: string; metodoPago?: string; statusDetail?: string } | null> {
    return this.http.get<{ estado?: string; metodoPago?: string; statusDetail?: string }>(
      `${this.base}/api/v1/pagos/pedido/${pedidoId}`
    );
  }

  crearPagoMercadoPago(payment: MercadoPagoPaymentRequest): Observable<MercadoPagoPaymentResponse> {
    return this.http.post<MercadoPagoPaymentResponse>(
      `${this.base}/api/v1/pagos/mercadopago/payment`, payment
    );
  }

  crearStripeCheckoutSession(ventaId: number): Observable<StripeCheckoutResponse> {
    return this.http.post<StripeCheckoutResponse>(`${this.base}/api/v1/pagos/stripe/checkout-session`, { ventaId });
  }

  consultarStripeSession(sessionId: string): Observable<StripeSessionResponse> {
    return this.http.get<StripeSessionResponse>(`${this.base}/api/v1/pagos/stripe/session/${encodeURIComponent(sessionId)}`);
  }
}
