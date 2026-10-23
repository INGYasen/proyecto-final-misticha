import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class SellerGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    const roles: string[] = this.auth.currentUser?.realm_access?.roles || [];
    const admin = roles.includes('SUPER_ADMIN') || roles.includes('ADMIN');
    const vendedora = roles.includes('VENDEDORA');

    if (vendedora && !admin) return true;
    if (admin) {
      this.router.navigate(['/admin']);
      return false;
    }
    this.router.navigate(['/']);
    return false;
  }
}
