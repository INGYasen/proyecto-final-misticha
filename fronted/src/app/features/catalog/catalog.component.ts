import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CatalogService } from '../../core/services/catalog.service';
import { CartService } from '../../core/services/cart.service';
import { FavoriteService } from '../../core/services/favorite.service';
import { ToastService } from '../../core/services/toast.service';
import { Prenda, Categoria } from '../../core/models/models';
import { FormBuilder, FormGroup } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css'],
})
export class CatalogComponent implements OnInit {
  loading = true;
  loadError = false;
  allProducts: Prenda[] = [];
  filtered: Prenda[] = [];
  categorias: Categoria[] = [];
  filterForm: FormGroup;

  readonly tallas = ['XS', 'S', 'M', 'L', 'XL', 'XXL'];
  readonly colores = ['Rojo', 'Azul', 'Verde', 'Ocre', 'Beige', 'Terracota', 'Negro', 'Blanco'];

  sortOption = 'default';
  showFilters = false; // mobile toggle

  get catalogTitle(): string {
    const publico = this.filterForm?.get('publico')?.value;
    const labels: Record<string, string> = {
      MUJER: 'CATEGORÍAS DE MUJER',
      VARON: 'CATEGORÍAS DE VARÓN',
      NINOS: 'CATEGORÍAS DE NIÑOS',
      UNISEX: 'CATEGORÍAS UNISEX',
    };
    return labels[publico] || 'TODAS LAS CATEGORÍAS';
  }

  constructor(
    private catalog: CatalogService,
    private cart: CartService,
    public favorites: FavoriteService,
    private toast: ToastService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
  ) {
    this.filterForm = this.fb.group({
      categoria: [''],
      publico:   [''],
      talla:     [''],
      color:     [''],
      busqueda:  [''],
    });
  }

  ngOnInit(): void {
    // Cargar categorías
    this.catalog.getCategoriasPublicas().subscribe({
      next: cats => this.categorias = cats,
      error: () => this.categorias = [
        { id: 1, nombre: 'Chalinas', descripcion: '' },
        { id: 2, nombre: 'Chompas', descripcion: '' },
        { id: 3, nombre: 'Chalecos', descripcion: '' },
        { id: 4, nombre: 'Gorros', descripcion: '' },
        { id: 5, nombre: 'Pantalones', descripcion: '' },
        { id: 6, nombre: 'Accesorios', descripcion: '' },
      ],
    });

    this.loadProducts();

    // Aplicar filtro de ruta si viene de categoría
    this.route.queryParams.subscribe(params => {
      this.filterForm.patchValue({
        categoria: params['categoria'] ? +params['categoria'] : '',
        publico: params['publico'] || '',
        busqueda: params['busqueda'] || '',
      });
    });

    // Reactivo: filtrar al cambiar
    this.filterForm.valueChanges.pipe(debounceTime(200), distinctUntilChanged()).subscribe(() => {
      this.applyFilters();
    });
  }

  loadProducts(): void {
    this.loading = true;
    this.loadError = false;
    this.catalog.getPrendas().subscribe({
      next: prds => {
        this.allProducts = prds;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error cargando catálogo público:', error);
        // No mostrar productos ficticios cuando el microservicio está caído:
        // ocultaba el problema y parecía que se habían perdido los productos reales.
        this.allProducts = [];
        this.applyFilters();
        this.loadError = true;
        this.loading = false;
        this.toast.show('No se pudo cargar el catálogo. Verifica ms-catalogo e intenta nuevamente.', 'error');
      },
    });
  }

  applyFilters(): void {
    const { categoria, publico, talla, color, busqueda } = this.filterForm.value;
    let result = [...this.allProducts];

    if (categoria) result = result.filter(p => p.idCategoria === +categoria);
    if (publico)   result = result.filter(p => p.publico === publico);
    if (talla)     result = result.filter(p => p.talla === talla);
    if (color)     result = result.filter(p => p.color?.toLowerCase().includes(color.toLowerCase()));
    if (busqueda)  result = result.filter(p =>
      p.nombre?.toLowerCase().includes(busqueda.toLowerCase()) ||
      p.material?.toLowerCase().includes(busqueda.toLowerCase())
    );

    // Sort
    if (this.sortOption === 'price-asc')  result.sort((a, b) => (a.precio||0) - (b.precio||0));
    if (this.sortOption === 'price-desc') result.sort((a, b) => (b.precio||0) - (a.precio||0));

    this.filtered = result;
  }

  onSort(opt: string): void {
    this.sortOption = opt;
    this.applyFilters();
  }

  clearFilters(): void { this.filterForm.reset(); }

  enPromo(p: Prenda): boolean {
    return !!p.precioBase && p.precio != null && p.precioBase > p.precio;
  }

  porcentajePromo(p: Prenda): number {
    if (!this.enPromo(p)) return 0;
    return Math.round((1 - (p.precio as number) / (p.precioBase as number)) * 100);
  }

  addToCart(p: Prenda): void {
    this.cart.addItem({
      prendaId: p.id, nombrePrenda: p.nombre || `Prenda #${p.id}`,
      precio: p.precio || 0, cantidad: 1,
      urlFoto: p.imagen, talla: p.talla, color: p.color,
    });
    this.cart.openCart();
    this.toast.show(`"${p.nombre}" agregado al carrito ✓`);
  }

  toggleFavorite(p: Prenda): void {
    const saved = this.favorites.toggle({
      prendaId: p.id,
      nombrePrenda: p.nombre || `Prenda #${p.id}`,
      precio: p.precio || 0,
      urlFoto: p.imagen,
      talla: p.talla,
      color: p.color,
    });
    this.toast.show(saved ? `"${p.nombre}" guardado en favoritos` : 'Quitado de favoritos');
  }

}
