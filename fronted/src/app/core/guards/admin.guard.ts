import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
    constructor(private auth: AuthService, private router: Router) {}

    canActivate(): boolean {
        const roles: string[] = this.auth.currentUser?.realm_access?.roles || [];
        const isAdmin = roles.includes('SUPER_ADMIN') || roles.includes('ADMIN');

        if (isAdmin) return true;
        if (roles.includes('VENDEDORA')) {
            this.router.navigate(['/vendedora']);
            return false;
        }

        this.router.navigate(['/']);
        return false;
    }
}
