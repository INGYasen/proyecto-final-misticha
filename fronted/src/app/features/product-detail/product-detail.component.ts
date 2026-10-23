import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CatalogService } from '../../core/services/catalog.service';
import { CartService } from '../../core/services/cart.service';
import { FavoriteService } from '../../core/services/favorite.service';
import { ToastService } from '../../core/services/toast.service';
import { Prenda, PrecioPrenda } from '../../core/models/models';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
    selector: 'app-product-detail',
    templateUrl: './product-detail.component.html',
    styleUrls: ['./product-detail.component.css'],
    standalone: true,
    imports: [CommonModule, RouterModule],
})
export class ProductDetailComponent implements OnInit {
    loading = true;
    error = false;
    producto: Prenda | null = null;
    precio: PrecioPrenda | null = null;
    quantity = 1;
    activeTab = 'descripcion';
    imgError = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private catalog: CatalogService,
        private cart: CartService,
        public favorites: FavoriteService,
        private toast: ToastService,
    ) {}

    ngOnInit(): void {
        // BUGFIX: parse id safely — avoid NaN / undefined crashing the route
        const rawId = this.route.snapshot.paramMap.get('id');
        const id = rawId ? parseInt(rawId, 10) : NaN;

        if (!id || isNaN(id)) {
            // Don't navigate to /producto/undefined — redirect to catalog instead
            this.router.navigate(['/catalogo']);
            return;
        }

        this.loadProduct(id);
    }

    private loadProduct(id: number): void {
        this.loading = true;
        this.error = false;

        // Load product from the catalog public endpoint
        this.catalog.getProductoById(id).pipe(
            catchError(() => {
                // Try the catalog item endpoint as fallback
                return this.catalog.getPrendaById(id).pipe(
                    catchError(() => of(null))
                );
            })
        ).subscribe({
            next: (producto) => {
                if (!producto) {
                    this.error = true;
                    this.loading = false;
                    return;
                }
                this.producto = producto;

                // Also fetch pricing if not embedded
                if (!producto.precio) {
                    this.catalog.getPrecioByPrenda(id).pipe(
                        catchError(() => of(null))
                    ).subscribe(precio => {
                        this.precio = precio;
                        if (precio && this.producto) {
                            this.producto = { ...this.producto, precio: precio.precioFinal ?? precio.precioBase };
                        }
                        this.loading = false;
                    });
                } else {
                    this.loading = false;
                }
            },
            error: () => {
                this.error = true;
                this.loading = false;
            }
        });
    }

    get precioMostrar(): number {
        return this.producto?.precio ?? this.precio?.precioFinal ?? this.precio?.precioBase ?? 0;
    }

    get tieneDescuento(): boolean {
        return !!(this.precio?.descuento && (this.precio.precioFinal ?? 0) < this.precio.precioBase);
    }

    get precioOriginal(): number {
        return this.precio?.precioBase ?? 0;
    }

    changeQty(delta: number): void {
        const max = this.producto?.stockDisponible || 99;
        this.quantity = Math.min(max, Math.max(1, this.quantity + delta));
    }

    addToCart(): void {
        if (!this.producto) return;
        if (!this.producto.nombre) {
            this.toast.show('Este producto no tiene nombre público aún', 'error');
            return;
        }
        this.cart.addItem({
            prendaId: this.producto.id,
            nombrePrenda: this.producto.nombre,
            precio: this.precioMostrar,
            cantidad: this.quantity,
            urlFoto: this.producto.imagen,
            talla: this.producto.talla,
            color: this.producto.color,
        });
        this.cart.openCart();
        this.toast.show(`"${this.producto.nombre}" agregado al carrito ✓`);
    }

    toggleFavorite(): void {
        if (!this.producto) return;
        const saved = this.favorites.toggle({
            prendaId: this.producto.id,
            nombrePrenda: this.producto.nombre || `Prenda #${this.producto.id}`,
            precio: this.precioMostrar,
            urlFoto: this.producto.imagen,
            talla: this.producto.talla,
            color: this.producto.color,
        });
        this.toast.show(saved ? 'Guardado en favoritos' : 'Quitado de favoritos');
    }

    onImgError(): void {
        this.imgError = true;
    }

    goBack(): void {
        this.router.navigate(['/catalogo']);
    }
}