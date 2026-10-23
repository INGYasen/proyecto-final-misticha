import { Component } from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {filter} from "rxjs";

@Component({
    selector: 'app-root',
    template: `
    <ng-container *ngIf="!isAdminRoute">
      <app-navbar></app-navbar>
      <app-cart-sidebar></app-cart-sidebar>
      <app-favorites-panel></app-favorites-panel>
      <app-profile-panel></app-profile-panel>
    </ng-container>
    <main>
      <router-outlet></router-outlet>
    </main>
    <ng-container *ngIf="!isAdminRoute">
      <app-footer></app-footer>
    </ng-container>
    <app-toast></app-toast>
  `,
})
export class AppComponent {
    isAdminRoute = false;

    constructor(private router: Router) {
        this.router.events.pipe(
            filter(e => e instanceof NavigationEnd)
        ).subscribe((e: any) => {
            const url = e.urlAfterRedirects;
            this.isAdminRoute = url.startsWith('/admin') || url.startsWith('/vendedora');
        });
    }
}
