import { NgModule, LOCALE_ID } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
registerLocaleData(localeEs, 'es');

import { AppRoutingModule } from './app-routing.module';
import { AppComponent }     from './app.component';

// Interceptor
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

// Guards
import { AdminGuard } from './core/guards/admin.guard';

// Pipes
import { FindMetodoPipe } from './shared/pipes/find-metodo.pipe';

// Shared components
import { NavbarComponent }      from './shared/components/navbar/navbar.component';
import { FooterComponent }      from './shared/components/footer/footer.component';
import { CartSidebarComponent } from './shared/components/cart-sidebar/cart-sidebar.component';
import { ToastComponent }       from './shared/components/toast/toast.component';
import { SpinnerComponent }     from './shared/components/spinner/spinner.component';

// ── NUEVO: Popup de promociones ──────────────────────────────────
import { PromoPopupComponent } from './shared/components/promo-popup/promo-popup.component';

// Features — Tienda
import { HomeComponent }          from './features/home/home.component';
import { CatalogComponent }       from './features/catalog/catalog.component';
import { ProductDetailComponent } from './features/product-detail/product-detail.component';
import { CartComponent }          from './features/cart/cart.component';
import { CheckoutComponent }      from './features/checkout/checkout.component';
import { LoginComponent }         from './features/auth/login/login.component';
import { RegisterComponent }      from './features/auth/register/register.component';
import { OrdersComponent }        from './features/orders/orders.component';
import { ContactComponent }       from './features/contact/contact.component';
import { FavoritesPanelComponent } from './shared/components/favorites-panel/favorites-panel.component';
import { ProfilePanelComponent } from './shared/components/profile-panel/profile-panel.component';
import { OrderSuccessComponent }  from './features/checkout/order-success/order-success.component';
import { PaymentResultComponent } from './features/checkout/payment-result/payment-result.component';

// ── NUEVO: Voucher dedicado ─────────────────────────────────────
import { VoucherComponent } from './features/voucher/voucher.component';

// Features — Admin
import { AdminComponent } from './features/admin/admin.component';

@NgModule({
    declarations: [
        AppComponent,
        FindMetodoPipe,
        NavbarComponent,
        FooterComponent,
        CartSidebarComponent,
        FavoritesPanelComponent,
        ProfilePanelComponent,
        ToastComponent,
        SpinnerComponent,
        // ── NUEVO ──
        // Tienda
        HomeComponent,
        CatalogComponent,
        CartComponent,
        CheckoutComponent,
        LoginComponent,
        RegisterComponent,
        OrdersComponent,
        ContactComponent,
        OrderSuccessComponent,
        PaymentResultComponent,
        // ── NUEVO ──
        VoucherComponent


    ],
    imports: [
        BrowserModule,
        CommonModule,
        HttpClientModule,
        ReactiveFormsModule,
        FormsModule,
        AppRoutingModule,
        PromoPopupComponent,
        ProductDetailComponent
    ],
    providers: [
        {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
        {provide: LOCALE_ID, useValue: 'es'},
        AdminGuard,
    ],
    bootstrap: [AppComponent],
    exports: [
        SpinnerComponent
    ]
})
export class AppModule {}
