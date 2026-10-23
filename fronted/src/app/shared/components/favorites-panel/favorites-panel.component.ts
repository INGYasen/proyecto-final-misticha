import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FavoriteItem, FavoriteService } from '../../../core/services/favorite.service';
import { CartService } from '../../../core/services/cart.service';
import { ProfilePanelService } from '../../../core/services/profile-panel.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-favorites-panel',
  templateUrl: './favorites-panel.component.html',
  styleUrls: ['./favorites-panel.component.css'],
})
export class FavoritesPanelComponent implements OnInit {
  items: FavoriteItem[] = [];
  open = false;

  constructor(
    public favorites: FavoriteService,
    private cart: CartService,
    private profile: ProfilePanelService,
    private toast: ToastService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.favorites.items$.subscribe(items => this.items = items);
    this.favorites.open$.subscribe(open => {
      this.open = open;
      if (open) this.cart.closeCart();
    });
    this.cart.open$.subscribe(open => { if (open) this.favorites.close(); });
    this.profile.open$.subscribe(open => { if (open) this.favorites.close(); });
  }

  close(): void { this.favorites.close(); }

  remove(item: FavoriteItem): void {
    this.favorites.remove(item.prendaId);
  }

  addToCart(item: FavoriteItem): void {
    this.cart.addItem({
      prendaId: item.prendaId,
      nombrePrenda: item.nombrePrenda,
      precio: item.precio,
      cantidad: 1,
      urlFoto: item.urlFoto,
      talla: item.talla,
      color: item.color,
    });
    this.favorites.close();
    this.cart.openCart();
    this.toast.show(`"${item.nombrePrenda}" pasó al carrito`);
  }

  goCatalog(): void {
    this.close();
    this.router.navigate(['/catalogo']);
  }
}
