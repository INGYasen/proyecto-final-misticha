import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CartItem } from '../models/models';
import { AuthService } from './auth.service';
import { OrderService } from './order.service';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly SHARED_KEY = 'raymisupreme_cart';
  private base = environment.apiUrl;
  private owner = 'invitado';
  private clienteId: number | null = null;

  private _items$ = new BehaviorSubject<CartItem[]>([]);
  items$ = this._items$.asObservable();

  private _open$ = new BehaviorSubject<boolean>(false);
  open$ = this._open$.asObservable();

  constructor(private http: HttpClient, private auth: AuthService, private orders: OrderService) {
    localStorage.removeItem(this.SHARED_KEY);
    this.auth.user$.subscribe(user => this.activate(user?.sub));
  }

  // ── Sidebar ───────────────────────────────────────────────────

  openCart(): void  { this._open$.next(true);  }
  closeCart(): void { this._open$.next(false); }
  toggleCart(): void { this._open$.next(!this._open$.value); }

  // ── Items ─────────────────────────────────────────────────────

  get items(): CartItem[] { return this._items$.value; }

  get total(): number {
    return this.items.reduce((sum, i) => sum + i.precio * i.cantidad, 0);
  }

  get count(): number {
    return this.items.reduce((sum, i) => sum + i.cantidad, 0);
  }

  addItem(item: CartItem): void {
    const current = [...this.items];
    const idx = current.findIndex(i => i.prendaId === item.prendaId);
    if (idx > -1) {
      current[idx] = { ...current[idx], cantidad: current[idx].cantidad + item.cantidad };
    } else {
      current.push(item);
    }
    this.update(current);
    this.enviarAlServidor(item.prendaId, item.cantidad);
  }

  updateQty(prendaId: number, cantidad: number): void {
    if (cantidad < 1) { this.removeItem(prendaId); return; }
    const current = this.items.map(i =>
      i.prendaId === prendaId ? { ...i, cantidad } : i
    );
    this.update(current);
    const guardado = this.items.find(i => i.prendaId === prendaId);
    this.fijarCantidadServidor(guardado);
  }

  removeItem(prendaId: number): void {
    const previo = this.items.find(i => i.prendaId === prendaId);
    this.update(this.items.filter(i => i.prendaId !== prendaId));
    if (previo) this.fijarCantidadServidor({ ...previo, cantidad: 0 });
  }

  clearCart(): void {
    this.update([]);
    if (!this.clienteId) return;
    this.http.delete(`${this.base}/api/v1/carrito/${this.clienteId}`).subscribe({ error: () => undefined });
  }

  /** El invitado ya eligió algo y quiere pagar. El login no debe perder esas prendas. */
  prepararPago(): void {
    if (this.count > 0) this.auth.marcarQuieroPagar();
  }

  // ── Backend sync (ms-carrito) ─────────────────────────────────

  /** Sincroniza carrito local con backend cuando el usuario inicia sesión */
  syncWithBackend(clienteId: number): Observable<any> {
    const items = this.items;
    // Usar el endpoint de convertir o simplemente agregar items
    return this.http.put(`${this.base}/api/v1/carrito/${clienteId}/convertir`, {});
  }

  addItemBackend(clienteId: number, item: CartItem): Observable<any> {
    return this.http.post(`${this.base}/api/v1/carrito/${clienteId}/items`, {
      prendaId:     item.prendaId,
      nombrePrenda: item.nombrePrenda,
      precio:       item.precio,
      cantidad:     item.cantidad,
    });
  }

  getCarritoBackend(clienteId: number): Observable<any> {
    return this.http.get(`${this.base}/api/v1/carrito/${clienteId}`);
  }

  // ── Persistencia local ────────────────────────────────────────

  private activate(sub?: string): void {
    const desdeInvitado = this.owner === 'invitado' && !!sub;
    const llevados = desdeInvitado ? this.items.map(item => ({ ...item })) : [];
    this.owner = sub || 'invitado';
    this.clienteId = null;
    const propios = this.read();
    this.update(desdeInvitado ? this.juntar(propios, llevados) : propios);
    if (desdeInvitado) localStorage.removeItem('raymisupreme_cart_invitado');
    this._open$.next(false);
    if (!sub) return;
    this.orders.getCurrentCliente().subscribe({
      next: cliente => {
        if (!cliente?.idCliente || this.owner !== sub) return;
        this.clienteId = cliente.idCliente;
        this.traerDelServidor();
      },
      error: () => undefined,
    });
  }

  private juntar(base: CartItem[], extra: CartItem[]): CartItem[] {
    const mapa = new Map<number, CartItem>();
    for (const item of base) mapa.set(item.prendaId, { ...item });
    for (const item of extra) {
      const previo = mapa.get(item.prendaId);
      if (!previo) {
        const copia = { ...item };
        delete copia.serverItemId;
        mapa.set(item.prendaId, copia);
        continue;
      }
      if (item.cantidad > previo.cantidad) previo.cantidad = item.cantidad;
      if (!previo.urlFoto && item.urlFoto) previo.urlFoto = item.urlFoto;
      if (!previo.talla && item.talla) previo.talla = item.talla;
      if (!previo.color && item.color) previo.color = item.color;
      if (!previo.nombrePrenda && item.nombrePrenda) previo.nombrePrenda = item.nombrePrenda;
    }
    return [...mapa.values()];
  }

  private traerDelServidor(): void {
    if (!this.clienteId) return;
    const clienteId = this.clienteId;
    const owner = this.owner;
    const local = this.items.map(item => ({ ...item }));
    this.http.get<any>(`${this.base}/api/v1/carrito/${clienteId}`).subscribe({
      next: carrito => {
        if (this.clienteId !== clienteId || this.owner !== owner) return;
        const remotos = Array.isArray(carrito?.items) ? carrito.items : [];
        if (!remotos.length) {
          local.forEach(item => this.enviarAlServidor(item.prendaId, item.cantidad));
          return;
        }
        const vista: CartItem[] = remotos.map((item: any) => {
          const previo = local.find(actual => actual.prendaId === Number(item.prendaId));
          return {
            prendaId: Number(item.prendaId),
            nombrePrenda: item.nombrePrenda || previo?.nombrePrenda,
            precio: Number(item.precio),
            cantidad: Number(item.cantidad),
            urlFoto: previo?.urlFoto,
            talla: previo?.talla,
            color: previo?.color,
            serverItemId: item.id,
          } as CartItem;
        });
        const juntos = this.juntar(vista, local);
        this.update(juntos);
        const enServidor = new Set(vista.map(item => item.prendaId));
        juntos.forEach(item => {
          if (!enServidor.has(item.prendaId)) {
            this.enviarAlServidor(item.prendaId, item.cantidad);
            return;
          }
          const remoto = vista.find(actual => actual.prendaId === item.prendaId);
          if (remoto && item.cantidad > remoto.cantidad) {
            this.fijarCantidadServidor({ ...item, serverItemId: remoto.serverItemId });
          }
        });
      },
      error: () => undefined,
    });
  }

  private enviarAlServidor(prendaId: number, cantidad: number): void {
    if (!this.clienteId || cantidad < 1) return;
    const item = this.items.find(actual => actual.prendaId === prendaId);
    if (!item) return;
    this.http.post<any>(`${this.base}/api/v1/carrito/${this.clienteId}/items`, {
      prendaId: item.prendaId,
      nombrePrenda: item.nombrePrenda,
      precio: item.precio,
      cantidad,
    }).subscribe({
      next: carrito => this.recordarIds(carrito),
      error: () => undefined,
    });
  }

  private fijarCantidadServidor(item?: CartItem): void {
    if (!this.clienteId || !item?.serverItemId) return;
    this.http.put(`${this.base}/api/v1/carrito/${this.clienteId}/items/${item.serverItemId}`, null, {
      params: { cantidad: item.cantidad },
    }).subscribe({ error: () => undefined });
  }

  private recordarIds(carrito: any): void {
    const remotos = Array.isArray(carrito?.items) ? carrito.items : [];
    if (!remotos.length) return;
    this.update(this.items.map(item => {
      const remoto = remotos.find((actual: any) => Number(actual.prendaId) === item.prendaId);
      return remoto ? { ...item, serverItemId: remoto.id } : item;
    }));
  }

  private key(): string {
    return `raymisupreme_cart_${this.owner}`;
  }

  private update(items: CartItem[]): void {
    this._items$.next(items);
    localStorage.setItem(this.key(), JSON.stringify(items));
  }

  private read(): CartItem[] {
    try {
      const parsed = JSON.parse(localStorage.getItem(this.key()) || '[]');
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }
}
