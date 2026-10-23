import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Descuento {
  id: number;
  codigo: string;
  descripcion?: string;
  porcentaje: number;
  fechaInicio: string;
  fechaFin: string;
  activo?: boolean;
  prendaIds?: number[];
  productos?: string[];
}

export type DescuentoRequest = Omit<Descuento, 'id' | 'activo'>;

@Injectable({ providedIn: 'root' })
export class DescuentoService {
  private readonly base = `${environment.apiUrl}/api/v1/precios/descuentos`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Descuento[]> { return this.http.get<Descuento[]>(this.base); }
  crear(data: DescuentoRequest): Observable<Descuento> { return this.http.post<Descuento>(this.base, data); }
  actualizar(id: number, data: DescuentoRequest): Observable<Descuento> { return this.http.put<Descuento>(`${this.base}/${id}`, data); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/${id}`); }
}
