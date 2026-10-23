import { Component, OnInit, HostListener } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { FavoriteService } from '../../../core/services/favorite.service';
import { ProfilePanelService } from '../../../core/services/profile-panel.service';
import { UserInfo } from '../../../core/models/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  isScrolled = false;
  isMobileMenuOpen = false;
  isLoggedIn = false;
  cartCount = 0;
  favCount = 0;
  favOpen = false;
  profileOpen = false;
  searchTerm = '';
  user: UserInfo | null = null;

  constructor(
    public auth: AuthService,
    public cart: CartService,
    private favorites: FavoriteService,
    private profile: ProfilePanelService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.auth.isLoggedIn$.subscribe(v => this.isLoggedIn = v);
    this.auth.user$.subscribe(u => this.user = u);
    this.cart.items$.subscribe(() => this.cartCount = this.cart.count);
    this.favorites.items$.subscribe(() => this.favCount = this.favorites.count);
    this.favorites.open$.subscribe(open => this.favOpen = open);
    this.profile.open$.subscribe(open => this.profileOpen = open);
  }

  @HostListener('window:scroll')
  onScroll(): void {
    this.isScrolled = window.scrollY > 60;
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  openCart(): void {
    this.favorites.close();
    this.profile.close();
    this.cart.openCart();
  }

  openFavorites(): void {
    this.profile.close();
    this.cart.closeCart();
    this.favorites.togglePanel();
  }

  entrarSinPagar(): void {
    this.auth.olvidarQuieroPagar();
  }

  openProfile(): void {
    if (!this.isLoggedIn) {
      this.auth.olvidarQuieroPagar();
      this.router.navigate(['/login']);
      return;
    }
    this.favorites.close();
    this.cart.closeCart();
    this.profile.toggle();
  }

  search(): void {
    const busqueda = this.searchTerm.trim();
    this.router.navigate(['/catalogo'], {
      queryParams: busqueda ? { busqueda } : {},
    });
  }

  logout(): void { this.auth.logout(); }

  get panelLink(): string | null {
    const roles = this.user?.realm_access?.roles || [];
    if (roles.includes('SUPER_ADMIN') || roles.includes('ADMIN')) return '/admin';
    if (roles.includes('VENDEDORA')) return '/vendedora';
    return null;
  }

  get panelLabel(): string {
    return this.panelLink === '/admin' ? 'Panel admin' : 'Panel vendedora';
  }
}
