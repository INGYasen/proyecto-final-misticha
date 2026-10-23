import { Component, OnInit } from '@angular/core';
import { CartService } from '../../../core/services/cart.service';
import { CartItem } from '../../../core/models/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart-sidebar',
  templateUrl: './cart-sidebar.component.html',
  styleUrls: ['./cart-sidebar.component.css'],
})
export class CartSidebarComponent implements OnInit {
  items: CartItem[] = [];
  isOpen = false;

  constructor(public cart: CartService, private router: Router) {}

  ngOnInit(): void {
    this.cart.items$.subscribe(items => this.items = items);
    this.cart.open$.subscribe(open => this.isOpen = open);
  }

  close(): void { this.cart.closeCart(); }

  updateQty(prendaId: number, delta: number): void {
    const item = this.items.find(i => i.prendaId === prendaId)!;
    this.cart.updateQty(prendaId, item.cantidad + delta);
  }

  remove(prendaId: number): void { this.cart.removeItem(prendaId); }

  goCheckout(): void {
    this.cart.closeCart();
    this.cart.prepararPago();
    this.router.navigate(['/checkout']);
  }

  get total(): number { return this.cart.total; }
}
