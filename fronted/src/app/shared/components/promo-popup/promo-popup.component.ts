import { Component, OnInit, OnDestroy } from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import {PromocionResponse, PromocionService} from "../../../core/services/promocion.service";
import {CartService} from "../../../core/services/cart.service";
import {ToastService} from "../../../core/services/toast.service";
import {CommonModule} from "@angular/common";

/**
 * PromoPopupComponent
 * Popup de oferta que aparece al entrar a la tienda.
 * Ubicar en: src/app/shared/components/promo-popup/
 *
 * Comportamiento:
 * - Aparece 1.5s después de cargar el home
 * - Se muestra UNA sola vez por sesión (sessionStorage)
 * - Carrusel si hay múltiples promociones
 * - Al hacer click → navega al detalle y cierra
 * - Al "Agregar al carrito" → aplica descuento automático
 */
@Component({
    selector: 'app-promo-popup',
    templateUrl: './promo-popup.component.html',
    styleUrls: ['./promo-popup.component.css'],
    standalone: true,

    imports: [CommonModule, RouterModule]
})
export class PromoPopupComponent implements OnInit, OnDestroy {
    visible = false;
    closing = false;
    promociones: PromocionResponse[] = [];
    currentIndex = 0;
    addingToCart = false;

    private autoCloseTimer?: any;
    private carouselTimer?: any;
    private readonly SESSION_KEY = 'raymisupreme_popup_shown';

    constructor(
        private promoService: PromocionService,
        private cart: CartService,
        private toast: ToastService,
        private router: Router,
    ) {}

    ngOnInit(): void {
        // No mostrar si ya se vio en esta sesión
        if (sessionStorage.getItem(this.SESSION_KEY)) return;

        this.promoService.getPopup(3).subscribe(promos => {
            if (!promos.length) return;
            this.promociones = promos;
            // Mostrar con delay de 1.5s para no ser intrusivo
            setTimeout(() => {
                this.visible = true;
                sessionStorage.setItem(this.SESSION_KEY, '1');
                this.startCarousel();
                // Auto-cerrar tras 12 segundos
                this.autoCloseTimer = setTimeout(() => this.cerrar(), 12_000);
            }, 1500);
        });
    }

    ngOnDestroy(): void {
        clearTimeout(this.autoCloseTimer);
        clearInterval(this.carouselTimer);
    }

    get promo(): PromocionResponse | undefined {
        return this.promociones[this.currentIndex];
    }

    goTo(index: number): void {
        this.currentIndex = index;
        this.resetCarousel();
    }

    next(): void {
        this.currentIndex = (this.currentIndex + 1) % this.promociones.length;
        this.resetCarousel();
    }

    prev(): void {
        this.currentIndex = (this.currentIndex - 1 + this.promociones.length) % this.promociones.length;
        this.resetCarousel();
    }

    cerrar(): void {
        this.closing = true;
        clearTimeout(this.autoCloseTimer);
        clearInterval(this.carouselTimer);
        setTimeout(() => { this.visible = false; this.closing = false; }, 350);
    }

    verProducto(): void {
        if (!this.promo) return;
        this.cerrar();
        setTimeout(() => this.router.navigate(['/producto', this.promo!.idPrenda]), 100);
    }

    agregarAlCarrito(): void {
        if (!this.promo || this.addingToCart) return;
        this.addingToCart = true;

        // Aplicar descuento real desde el microservicio
        this.promoService.aplicarDescuento(this.promo.idPrenda, this.promo.precioBase)
            .subscribe(res => {
                this.cart.addItem({
                    prendaId:     this.promo!.idPrenda,
                    nombrePrenda: this.promo!.nombrePrenda,
                    precio:       res.precioFinal,          // precio YA con descuento
                    cantidad:     1,
                    urlFoto:      this.promo!.urlImagen,
                });

                const msg = res.tieneDescuento
                    ? `¡Agregado con ${res.porcentajeDescuento}% de descuento! 🎉`
                    : '¡Producto agregado al carrito!';
                this.toast.show(msg);
                this.addingToCart = false;
                this.cerrar();
                this.cart.openCart();
            });
    }

    private startCarousel(): void {
        if (this.promociones.length <= 1) return;
        this.carouselTimer = setInterval(() => {
            this.currentIndex = (this.currentIndex + 1) % this.promociones.length;
        }, 4000);
    }

    private resetCarousel(): void {
        clearInterval(this.carouselTimer);
        this.startCarousel();
    }

    getTipoLabel(tipo: string): string {
        const map: Record<string, string> = {
            TEMPORADA: '❄️ Temporada',
            LIQUIDACION: '🔥 Liquidación',
            LANZAMIENTO: '🎉 Nuevo',
            DESTACADO: '⭐ Destacado',
        };
        return map[tipo] || tipo;
    }
}
