import { Component, OnDestroy, OnInit } from '@angular/core';
import { CatalogService } from '../../core/services/catalog.service';
import { CartService } from '../../core/services/cart.service';
import { FavoriteService } from '../../core/services/favorite.service';
import { ToastService } from '../../core/services/toast.service';
import { CatalogoItem, Categoria, Promocion, Prenda } from '../../core/models/models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit, OnDestroy {
  loading = true;
  productos: Prenda[] = [];
  categorias: Categoria[] = [];
  promociones: Promocion[] = [];
  slideIndex = 0;
  private slideTimer?: ReturnType<typeof setTimeout>;
  private readonly autoMs = 5000;
  private readonly graceMs = 10000;

  readonly slides = [
    { nombre: 'Gorro alpaca oso', imagen: 'assets/carrusel/01-gorro-oso.jpg', seccion: 'Gorros' },
    { nombre: 'Poncho rayas del Cusco', imagen: 'assets/carrusel/02-poncho-rayas.jpg', seccion: 'Ponchos' },
    { nombre: 'Chompa burdeo bolsillo andino', imagen: 'assets/carrusel/03-chompa-burdeo.jpg', seccion: 'Chompas' },
    { nombre: 'Chalina rosa de flecos', imagen: 'assets/carrusel/04-chalina-rosa.jpg', seccion: 'Chalinas' },
    { nombre: 'Conjunto festivo de Puno', imagen: 'assets/carrusel/05-conjunto-festivo.jpg', seccion: 'Conjuntos' },
    { nombre: 'Chompa negra lobos andinos', imagen: 'assets/carrusel/06-chompa-lobos.jpg', seccion: 'Chompas' },
    { nombre: 'Chompa roja lago Titicaca', imagen: 'assets/carrusel/07-chompa-roja.jpg', seccion: 'Chompas' },
    { nombre: 'Casaca denim manga andina', imagen: 'assets/carrusel/08-casaca-denim.jpg', seccion: 'Casacas', encuadre: 'center top' },
  ];

  // Productos destacados (primeros 8)
  get destacados(): Prenda[] { return this.productos.slice(0, 8); }

  enPromo(p: Prenda): boolean {
    return !!p.precioBase && p.precio != null && p.precioBase > p.precio;
  }

  porcentajePromo(p: Prenda): number {
    if (!this.enPromo(p)) return 0;
    return Math.round((1 - (p.precio as number) / (p.precioBase as number)) * 100);
  }

  constructor(
    private catalog: CatalogService,
    private cart: CartService,
    public favorites: FavoriteService,
    private toastSvc: ToastService,
  ) {}

  ngOnInit(): void {
    this.scheduleSlide(this.autoMs);
    forkJoin({
      productos:   this.catalog.getProductosPublicos(),
      categorias:  this.catalog.getCategoriasPublicas(),
      promociones: this.catalog.getPromocionesPublicas(),
    }).subscribe({
      next: ({ productos, categorias, promociones }) => {
        this.productos   = productos;
        this.categorias  = categorias;
        this.promociones = promociones;
        this.loading = false;
      },
      error: () => {
        // Si el backend no está levantado, usa datos de muestra
        this.loadMockData();
        this.loading = false;
      },
    });
  }

  ngOnDestroy(): void {
    this.clearSlideTimer();
  }

  nextSlide(): void { this.moveSlide(1); }
  previousSlide(): void { this.moveSlide(-1); }

  goToSlide(index: number): void {
    this.slideIndex = index;
    this.scheduleSlide(this.graceMs);
  }

  private moveSlide(step: number): void {
    this.slideIndex = (this.slideIndex + step + this.slides.length) % this.slides.length;
    this.scheduleSlide(this.graceMs);
  }

  private scheduleSlide(delay: number): void {
    this.clearSlideTimer();
    this.slideTimer = setTimeout(() => {
      this.slideIndex = (this.slideIndex + 1) % this.slides.length;
      this.scheduleSlide(this.autoMs);
    }, delay);
  }

  private clearSlideTimer(): void {
    if (this.slideTimer) clearTimeout(this.slideTimer);
  }

  addToCart(producto: Prenda): void {
    this.cart.addItem({
      prendaId:     producto.id,
      nombrePrenda: producto.nombre || `Prenda #${producto.id}`,
      precio:       producto.precio || 0,
      cantidad:     1,
      urlFoto:      producto.imagen,
      talla:        producto.talla,
      color:        producto.color,
    });
    this.cart.openCart();
    this.toastSvc.show(`"${producto.nombre}" agregado al carrito ✓`);
  }

  toggleFavorite(producto: Prenda): void {
    const saved = this.favorites.toggle({
      prendaId: producto.id,
      nombrePrenda: producto.nombre || `Prenda #${producto.id}`,
      precio: producto.precio || 0,
      urlFoto: producto.imagen,
      talla: producto.talla,
      color: producto.color,
    });
    this.toastSvc.show(saved ? `"${producto.nombre}" guardado en favoritos` : 'Quitado de favoritos');
  }

  private loadMockData(): void {
    this.categorias = [
      { id: 1, nombre: 'Chalinas', descripcion: 'Chalinas tejidas a mano' },
      { id: 2, nombre: 'Chompas', descripcion: 'Chompas de alpaca' },
      { id: 3, nombre: 'Chalecos', descripcion: 'Chalecos andinos' },
      { id: 4, nombre: 'Gorros', descripcion: 'Gorros de lana' },
    ];
    this.productos = Array.from({ length: 8 }, (_, i) => ({
      id: i + 1, idCategoria: (i % 4) + 1,
      codigoSku: `SKU-00${i+1}`, talla: ['S','M','L','XL'][i%4],
      color: ['Rojo','Azul','Verde','Ocre','Beige','Terracota','Negro','Blanco'][i],
      material: 'Lana de alpaca', stockDisponible: 10,
      nombre: this.slides[i].nombre,
      precio: [79.9, 189.9, 159.9, 95, 320, 149.9, 169.9, 199.9][i],
      imagen: this.slides[i].imagen,
      destacado: i < 4,
    }));
  }
}
