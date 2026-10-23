import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent }          from './features/home/home.component';
import { CatalogComponent }       from './features/catalog/catalog.component';
import { ProductDetailComponent } from './features/product-detail/product-detail.component';
import { CartComponent }          from './features/cart/cart.component';
import { CheckoutComponent }      from './features/checkout/checkout.component';
import { OrderSuccessComponent }  from './features/checkout/order-success/order-success.component';
import { PaymentResultComponent } from './features/checkout/payment-result/payment-result.component';
import { VoucherComponent }       from './features/voucher/voucher.component';
import { LoginComponent }         from './features/auth/login/login.component';
import { RegisterComponent }      from './features/auth/register/register.component';
import { OrdersComponent }        from './features/orders/orders.component';
import { ContactComponent }       from './features/contact/contact.component';
import { AdminComponent }         from './features/admin/admin.component';
import { AuthGuard }              from './core/guards/auth.guard';
import { AdminGuard }             from './core/guards/admin.guard';
import { SellerGuard }            from './core/guards/seller.guard';

const routes: Routes = [
    // ── Tienda pública ──────────────────────────────────────────────
    { path: '',              component: HomeComponent },
    { path: 'catalogo',      component: CatalogComponent },
    { path: 'contacto',      component: ContactComponent },
    { path: 'producto/:id',  component: ProductDetailComponent },
    { path: 'carrito',       component: CartComponent },

    // ── Checkout (requiere login) ────────────────────────────────────
    { path: 'checkout',            component: CheckoutComponent,    canActivate: [AuthGuard] },
    { path: 'pedido-exitoso/:id',  component: OrderSuccessComponent, canActivate: [AuthGuard] },
    { path: 'checkout/success', component: PaymentResultComponent, data: { title: 'Pago recibido', message: 'Mercado Pago devolvió el pago como aprobado. La confirmación automática se habilitará al implementar el webhook.' } },
    { path: 'checkout/failure', component: PaymentResultComponent, data: { title: 'Pago no completado', message: 'El pago no pudo completarse. Puedes volver a intentarlo desde tu pedido.' } },
    { path: 'checkout/pending', component: PaymentResultComponent, data: { title: 'Pago pendiente', message: 'Mercado Pago está procesando el pago. El pedido se confirmará cuando el cobro quede aprobado.' } },
    { path: 'checkout/cancel', component: PaymentResultComponent, data: { title: 'Pago cancelado', message: 'No se realizó ningún cobro. Puedes regresar al checkout e intentarlo nuevamente.' } },

    // ── Voucher — ruta NUEVA dedicada al comprobante ─────────────────
    { path: 'voucher/:id', component: VoucherComponent, canActivate: [AuthGuard] },

    // ── Auth ─────────────────────────────────────────────────────────
    { path: 'login',    component: LoginComponent },
    { path: 'registro', component: RegisterComponent },

    // ── Usuario autenticado ──────────────────────────────────────────
    { path: 'mis-pedidos', component: OrdersComponent, canActivate: [AuthGuard] },

    // ── Panel Admin ──────────────────────────────────────────────────
    { path: 'admin', component: AdminComponent, canActivate: [AdminGuard], data: { panel: 'admin' } },
    { path: 'vendedora', component: AdminComponent, canActivate: [SellerGuard], data: { panel: 'vendedora' } },

    // ── Fallback ─────────────────────────────────────────────────────
    { path: '**', redirectTo: '' },
];

@NgModule({
    imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled', anchorScrolling: 'enabled' })],
    exports: [RouterModule],
})
export class AppRoutingModule {}
