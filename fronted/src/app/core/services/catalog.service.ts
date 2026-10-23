import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CatalogoItem, Categoria, Prenda, HomeData, Promocion, PrecioPrenda } from '../models/models';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ── Home (público) ───────────────────────────────────────────

  getHome(): Observable<HomeData> {
    return this.http.get<HomeData>(`${this.base}/api/catalogo/public/home`);
  }

  getPromocionesPublicas(): Observable<Promocion[]> {
    return this.http.get<Promocion[]>(`${this.base}/api/catalogo/public/promociones`);
  }

  // ── Prendas públicas (catálogo v1) ───────────────────────────

  getPrendas(filtros?: {
    idCategoria?: number;
    publico?: string;
    material?: string;
    color?: string;
    talla?: string;
  }): Observable<Prenda[]> {
    let params = new HttpParams();
    if (filtros?.idCategoria) params = params.set('idCategoria', filtros.idCategoria);
    if (filtros?.publico)     params = params.set('publico',     filtros.publico);
    if (filtros?.material)    params = params.set('material',    filtros.material);
    if (filtros?.color)       params = params.set('color',       filtros.color);
    if (filtros?.talla)       params = params.set('talla',       filtros.talla);
    return this.http.get<any[]>(`${this.base}/api/catalogo/public/productos`, { params }).pipe(
      map(productos => productos.map(producto => ({
        id: producto.id,
        idCategoria: producto.categoria?.id,
        publico: producto.publico,
        codigoSku: producto.codigoUnico || '',
        talla: producto.talla,
        color: producto.color,
        material: producto.material,
        stockDisponible: producto.stockDisponible,
        nombre: producto.nombre,
        descripcionPublica: producto.descripcion,
        imagen: this.nitida(producto.imagen),
        precio: producto.precio,
        precioBase: producto.precioBase,
        destacado: producto.destacado,
        activo: producto.disponible,
      } as Prenda)))
    );
  }

  getPrendaById(id: number): Observable<Prenda> {
    return this.http.get<Prenda>(`${this.base}/api/v1/catalogo/prendas/${id}`);
  }

  getProductosPublicos(): Observable<Prenda[]> {
    return this.http.get<Prenda[]>(`${this.base}/api/catalogo/public/productos`).pipe(
      map(productos => productos.map(producto => ({ ...producto, imagen: this.nitida(producto.imagen) })))
    );
  }

  getProductoById(id: number): Observable<Prenda> {
    return this.http.get<Prenda>(`${this.base}/api/catalogo/public/productos/${id}`).pipe(
      map(producto => ({ ...producto, imagen: this.nitida(producto.imagen) }))
    );
  }

  private nitida(url?: string): string | undefined {
    if (!url || url.includes('?')) return url;
    return url.startsWith('assets/catalogo/') ? `${url}?v=3` : url;
  }

  getCategoriasPublicas(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.base}/api/catalogo/public/categorias`);
  }

  // ── Catálogo items (requiere auth VENDEDORA+) ─────────────────

  getCatalogoItems(): Observable<CatalogoItem[]> {
    return this.http.get<CatalogoItem[]>(`${this.base}/api/v1/catalogo/items`);
  }

  getCatalogoItemById(id: number): Observable<CatalogoItem> {
    return this.http.get<CatalogoItem>(`${this.base}/api/v1/catalogo/items/${id}`);
  }

  // ── Precios ───────────────────────────────────────────────────

  getPrecioByPrenda(prendaId: number): Observable<PrecioPrenda> {
    return this.http.get<PrecioPrenda>(`${this.base}/api/v1/precios/prendas/${prendaId}`);
  }
}
