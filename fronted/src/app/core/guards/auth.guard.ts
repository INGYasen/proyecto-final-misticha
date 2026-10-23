import { Injectable } from '@angular/core';
import { CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CartService } from '../services/cart.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private cart: CartService, private router: Router) {}

  canActivate(_route: unknown, state: RouterStateSnapshot): boolean {
    if (this.auth.isLoggedIn()) return true;
    if (state.url.startsWith('/checkout') && this.cart.count > 0) this.auth.marcarQuieroPagar();
    sessionStorage.setItem('raymisupreme_auth_return_url', state.url);
    this.router.navigate(['/login']);
    return false;
  }
}
