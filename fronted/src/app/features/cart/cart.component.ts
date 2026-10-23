import { Component, OnInit } from '@angular/core';
import { CartService } from '../../core/services/cart.service';
import { CartItem } from '../../core/models/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  items: CartItem[] = [];

  constructor(public cart: CartService, private router: Router) {}

  ngOnInit(): void {
    this.cart.items$.subscribe(items => this.items = items);
  }

  updateQty(prendaId: number, delta: number): void {
    const item = this.items.find(i => i.prendaId === prendaId)!;
    this.cart.updateQty(prendaId, item.cantidad + delta);
  }

  remove(prendaId: number): void { this.cart.removeItem(prendaId); }
  clearAll(): void { this.cart.clearCart(); }

  get total(): number { return this.cart.total; }
  get count(): number { return this.cart.count; }

  goCheckout(): void {
    this.cart.prepararPago();
    this.router.navigate(['/checkout']);
  }
}
