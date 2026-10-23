import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {environment} from "../../../environments/environment";

export interface PromocionResponse {
    idPromocion: number;
    idPrenda: number;
    titulo: string;
    descripcion: string;
    porcentajeDescuento: number;
    precioBase: number;
    precioFinal: number;
    urlImagen?: string;
    nombrePrenda: string;
    fechaInicio: string;
    fechaFin: string;
    activa: boolean;
    vigente: boolean;
    tipo: 'TEMPORADA' | 'LIQUIDACION' | 'LANZAMIENTO' | 'DESTACADO';
}

export interface AplicarDescuentoResponse {
    idPrenda: number;
    idPromocion?: number;
    precioOriginal: number;
    porcentajeDescuento: number;
    precioFinal: number;
    nombrePromocion?: string;
    tieneDescuento: boolean;
}

@Injectable({ providedIn: 'root' })
export class PromocionService {
    private base = environment.apiUrl;
    constructor(private http: HttpClient) {}

    getPopup(limite = 3): Observable<PromocionResponse[]> {
        return this.http
            .get<PromocionResponse[]>(`${this.base}/api/promociones/public/popup?limite=${limite}`)
            .pipe(catchError(() => of([])));
    }

    getVigentes(): Observable<PromocionResponse[]> {
        return this.http
            .get<PromocionResponse[]>(`${this.base}/api/promociones/public/vigentes`)
            .pipe(catchError(() => of([])));
    }

    aplicarDescuento(idPrenda: number, precioOriginal: number): Observable<AplicarDescuentoResponse> {
        return this.http
            .post<AplicarDescuentoResponse>(`${this.base}/api/promociones/public/aplicar-descuento`, { idPrenda, precioOriginal })
            .pipe(catchError(() => of({ idPrenda, precioOriginal, precioFinal: precioOriginal, porcentajeDescuento: 0, tieneDescuento: false } as AplicarDescuentoResponse)));
    }

    getTodas(): Observable<PromocionResponse[]> {
        return this.http.get<PromocionResponse[]>(`${this.base}/api/v1/promociones`);
    }
    crear(data: any): Observable<PromocionResponse> {
        return this.http.post<PromocionResponse>(`${this.base}/api/v1/promociones`, data);
    }
    actualizar(id: number, data: any): Observable<PromocionResponse> {
        return this.http.put<PromocionResponse>(`${this.base}/api/v1/promociones/${id}`, data);
    }
    desactivar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.base}/api/v1/promociones/${id}`);
    }

    private mockPromociones(): PromocionResponse[] {
        const now = new Date().toISOString();
        const end = new Date(Date.now() + 30 * 86400000).toISOString();
        return [
            { idPromocion: 1, idPrenda: 12, titulo: 'Temporada Frío — 20% OFF',
                descripcion: 'Chalinas de alpaca artesanales. Tejidas a mano por artesanas de Puno.',
                porcentajeDescuento: 20, precioBase: 89, precioFinal: 71.20,
                urlImagen: 'assets/catalogo/01.jpg?v=3',
                nombrePrenda: 'Chalina clásica', fechaInicio: now, fechaFin: end,
                activa: true, vigente: true, tipo: 'TEMPORADA' },
            { idPromocion: 2, idPrenda: 15, titulo: 'Lanzamiento — 15% OFF',
                descripcion: 'Chompas andinas tejidas a mano. Edición de temporada.',
                porcentajeDescuento: 15, precioBase: 159, precioFinal: 135.15,
                urlImagen: 'assets/catalogo/04.jpg?v=3',
                nombrePrenda: 'Chompa andina', fechaInicio: now, fechaFin: end,
                activa: true, vigente: true, tipo: 'LANZAMIENTO' },
            { idPromocion: 3, idPrenda: 13, titulo: 'Poncho andino — 10% OFF',
                descripcion: 'Ponchos de la colección. Tejidos con lana de alpaca.',
                porcentajeDescuento: 10, precioBase: 179, precioFinal: 161.10,
                urlImagen: 'assets/catalogo/02.jpg?v=3',
                nombrePrenda: 'Poncho andino', fechaInicio: now, fechaFin: end,
                activa: true, vigente: true, tipo: 'DESTACADO' },
        ];
    }
}
