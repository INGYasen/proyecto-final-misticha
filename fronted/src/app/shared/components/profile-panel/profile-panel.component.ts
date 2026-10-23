import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Cliente, UserInfo } from '../../../core/models/models';
import { AuthService } from '../../../core/services/auth.service';
import { OrderService } from '../../../core/services/order.service';
import { ProfilePanelService } from '../../../core/services/profile-panel.service';
import { FavoriteService } from '../../../core/services/favorite.service';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-profile-panel',
  templateUrl: './profile-panel.component.html',
  styleUrls: ['./profile-panel.component.css'],
})
export class ProfilePanelComponent implements OnInit {
  open = false;
  loading = false;
  user: UserInfo | null = null;
  cliente: Cliente | null = null;

  constructor(
    private panel: ProfilePanelService,
    private auth: AuthService,
    private orders: OrderService,
    private favorites: FavoriteService,
    private cart: CartService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.auth.user$.subscribe(user => this.user = user);
    this.panel.open$.subscribe(open => {
      this.open = open;
      if (open) {
        this.cart.closeCart();
        this.loadProfile();
      }
    });
    this.cart.open$.subscribe(open => { if (open) this.panel.close(); });
    this.favorites.open$.subscribe(open => { if (open) this.panel.close(); });
  }

  close(): void { this.panel.close(); }

  get displayName(): string {
    const fromCliente = [this.cliente?.nombres, this.cliente?.apellidos].filter(Boolean).join(' ').trim();
    if (fromCliente) return fromCliente;
    const fromToken = [this.user?.given_name, this.user?.family_name].filter(Boolean).join(' ').trim();
    return fromToken || this.user?.preferred_username || 'Cliente';
  }

  get initials(): string {
    const parts = this.displayName.split(' ').filter(Boolean);
    return ((parts[0]?.[0] || '') + (parts[1]?.[0] || '')).toUpperCase() || 'RS';
  }

  get username(): string {
    return this.cliente?.username || this.user?.preferred_username || '';
  }

  get email(): string {
    return this.cliente?.email || this.user?.email || 'Sin correo registrado';
  }

  goOrders(): void {
    this.close();
    this.router.navigate(['/mis-pedidos']);
  }

  logout(): void {
    this.close();
    this.auth.logout();
  }

  private loadProfile(): void {
    const cached = this.orders.getCachedCliente();
    if (cached) this.cliente = cached;
    this.loading = !cached;
    this.orders.getCurrentCliente().subscribe({
      next: cliente => {
        this.cliente = cliente;
        this.orders.cacheCurrentCliente(cliente);
        this.loading = false;
      },
      error: () => { this.loading = false; },
    });
  }
}
