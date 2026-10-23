import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Pedido } from '../../../core/models/models';

@Component({
  selector: 'app-order-success',
  templateUrl: './order-success.component.html',
  styleUrls: ['./order-success.component.css'],
})
export class OrderSuccessComponent implements OnInit {
  pedidoId: number | null = null;
  pedido: Pedido | null = null;

  constructor(
    private route: ActivatedRoute,
    private order: OrderService,
  ) {}

  ngOnInit(): void {
    this.pedidoId = +this.route.snapshot.paramMap.get('id')!;
    if (this.pedidoId) {
      this.order.getPedido(this.pedidoId).subscribe({
        next: p => this.pedido = p,
        error: () => { /* no critical */ },
      });
    }
  }
}
