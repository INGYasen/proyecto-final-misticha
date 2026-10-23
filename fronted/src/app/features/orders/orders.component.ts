import { Component, OnDestroy, OnInit } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { OrderService } from '../../core/services/order.service';
import { ToastService } from '../../core/services/toast.service';
import { CatalogService } from '../../core/services/catalog.service';
import { DetallePedido, Pedido } from '../../core/models/models';

interface LineaPedido extends DetallePedido {
  nombre: string;
  imagen?: string;
  subtotal: number;
}

interface PedidoVista extends Pedido {
  etiqueta?: string;
  detallePago?: string;
  metodo?: string;
}

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit, OnDestroy {
  pedidos: PedidoVista[] = [];
  loading = true;
  loadError = false;
  expandedId: number | null = null;
  detalleId: number | null = null;
  detalleLoading = false;
  lineas: LineaPedido[] = [];
  private sondeo?: ReturnType<typeof setInterval>;

  readonly estadoConfig: Record<string, { color: string; label: string; icon: string }> = {
    PENDIENTE:   { color: '#f39c12', label: 'Procesando pago…',  icon: '⏳' },
    CONFIRMADO:  { color: '#3498db', label: 'Confirmado', icon: '✅' },
    PAGADO:      { color: '#1f8a4c', label: 'Pagado',     icon: '✅' },
    ENVIADO:     { color: '#9b59b6', label: 'En camino',  icon: '🚚' },
    ENTREGADO:   { color: '#27ae60', label: 'Entregado',  icon: '📦' },
    CANCELADO:   { color: '#888', label: 'Compra cancelada',  icon: '✕' },
  };

  constructor(
    private order: OrderService,
    private catalog: CatalogService,
    private toast: ToastService,
  ) {}

  ngOnInit(): void {
    this.cargar();
    this.sondeo = setInterval(() => {
      if (this.pedidos.some(pedido => pedido.estadoPedido === 'PENDIENTE')) this.cargar(true);
    }, 4000);
  }

  ngOnDestroy(): void {
    if (this.sondeo) clearInterval(this.sondeo);
  }

  cargar(silencioso = false): void {
    if (!silencioso) this.loading = true;
    this.loadError = false;
    this.order.getMisPedidos().subscribe({
      next: pedidos => {
        const lista = pedidos || [];
        if (!lista.length) {
          this.pedidos = [];
          this.loading = false;
          return;
        }
        forkJoin(lista.map(pedido =>
          this.order.pagoDePedido(pedido.idPedido!).pipe(catchError(() => of(null)))
        )).subscribe(pagos => {
          this.pedidos = lista
            .map((pedido, index) => this.conPago(pedido, pagos[index]))
            .filter((pedido): pedido is PedidoVista => !!pedido);
          this.loading = false;
        });
      },
      error: () => {
        this.pedidos = [];
        this.loadError = true;
        this.loading = false;
      },
    });
  }

  pendiente(pedido: PedidoVista): boolean {
    return pedido.estadoPedido === 'PENDIENTE' && pedido.etiqueta !== 'Pago rechazado';
  }

  tono(pedido: PedidoVista): string {
    if (pedido.etiqueta === 'Pago rechazado') return '#e74c3c';
    if (pedido.etiqueta === 'Pagado') return '#1f8a4c';
    return this.getEstado(pedido.estadoPedido).color;
  }

  cancelar(pedido: Pedido): void {
    const id = pedido.idPedido;
    if (!id || !this.pendiente(pedido)) return;
    this.order.cancelarPedido(id).subscribe({
      next: () => {
        this.pedidos = this.pedidos.filter(item => item.idPedido !== id);
        this.toast.show('La compra quedó cancelada y ya no aparece en tus pedidos.');
      },
      error: () => this.toast.show('No se pudo cancelar la compra.', 'error'),
    });
  }

  toggleExpand(id: number): void {
    this.expandedId = this.expandedId === id ? null : id;
  }

  verDetalle(pedido: Pedido): void {
    const id = pedido.idPedido;
    if (!id) return;
    if (this.detalleId === id) {
      this.detalleId = null;
      this.lineas = [];
      return;
    }
    this.detalleId = id;
    this.expandedId = id;
    this.detalleLoading = true;
    this.lineas = [];
    this.order.getDetallesPedido(id).subscribe({
      next: detalles => this.resolverPrendas(detalles || []),
      error: () => {
        this.detalleLoading = false;
        this.lineas = [];
      },
    });
  }

  private resolverPrendas(detalles: DetallePedido[]): void {
    if (!detalles.length) {
      this.detalleLoading = false;
      this.lineas = [];
      return;
    }
    forkJoin(detalles.map(detalle =>
      this.catalog.getProductoById(detalle.idPrenda).pipe(catchError(() => of(null)))
    )).subscribe(productos => {
      this.lineas = detalles.map((detalle, index) => {
        const producto = productos[index];
        const precio = Number(detalle.precioUnitario) || 0;
        return {
          ...detalle,
          nombre: producto?.nombre || `Prenda #${detalle.idPrenda}`,
          imagen: producto?.imagen,
          subtotal: precio * (detalle.cantidad || 0),
        };
      });
      this.detalleLoading = false;
    });
  }

  private conPago(pedido: Pedido, pago: { estado?: string; metodoPago?: string; statusDetail?: string } | null): PedidoVista | null {
    const estadoPago = (pago?.estado || '').toUpperCase();
    const cobrado = ['PAGADO', 'CONFIRMADO', 'ENVIADO', 'ENTREGADO'].includes(pedido.estadoPedido)
      || ['APROBADO', 'CONFIRMADO', 'EXITOSO', 'PAGADO'].includes(estadoPago);
    const rechazado = ['RECHAZADO', 'REJECTED'].includes(estadoPago);
    const enProceso = ['PENDIENTE', 'PENDING', 'IN_PROCESS'].includes(estadoPago);
    if (!cobrado && !pago) return null;
    if (!cobrado && !rechazado && !enProceso) return null;
    const vista: PedidoVista = { ...pedido };
    if (cobrado) {
      vista.etiqueta = 'Pagado';
      vista.metodo = this.nombreMetodo(pago?.metodoPago);
    } else if (rechazado) {
      vista.etiqueta = 'Pago rechazado';
      vista.detallePago = this.motivoRechazo(pago?.statusDetail);
      vista.metodo = this.nombreMetodo(pago?.metodoPago);
    }
    return vista;
  }

  private nombreMetodo(metodo?: string): string {
    const valor = (metodo || '').toLowerCase();
    if (!valor) return '';
    if (valor.includes('yape')) return 'Yape';
    if (valor.includes('stripe')) return 'Tarjeta';
    return 'Tarjeta';
  }

  private motivoRechazo(detalle?: string): string {
    const codigo = (detalle || '').toLowerCase();
    if (!codigo) return 'El medio de pago no aprobó el cobro.';
    if (codigo.includes('insufficient')) return 'No había saldo suficiente.';
    if (codigo.includes('high_risk') || codigo.includes('blacklist')) return 'El medio de pago lo frenó por seguridad.';
    if (codigo.includes('security_code') || codigo.includes('bad_filled')) return 'Los datos de la tarjeta no coincidían.';
    if (codigo.includes('call_for_authorize')) return 'El banco pidió autorización y no se completó.';
    if (codigo.includes('max_attempts')) return 'Se superó el número de intentos.';
    return 'El medio de pago no aprobó el cobro.';
  }

  getEstado(estado: string) {
    return this.estadoConfig[estado] || { color: '#888', label: estado, icon: '📋' };
  }

  isStepDone(current: string, step: string): boolean {
    const rank: Record<string, number> = {
      PENDIENTE: 0, CONFIRMADO: 1, PAGADO: 2, ENVIADO: 2, ENTREGADO: 3,
    };
    return (rank[current] ?? -1) > (rank[step] ?? 99);
  }

  isStepCurrent(current: string, step: string): boolean {
    if (current === 'PAGADO') return step === 'CONFIRMADO';
    return current === step;
  }
}
