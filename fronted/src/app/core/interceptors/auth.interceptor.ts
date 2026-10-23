import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent, HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Solo agrega token a llamadas al API de nuestra app (no a Keycloak)
    const isApi = req.url.startsWith(environment.apiUrl);
    // Las rutas públicas deben funcionar aunque el navegador conserve un JWT
    // vencido. Si lo enviamos, el resource server puede responder 401 antes de
    // que Spring evalúe el permitAll del catálogo.
    const isPublicApi = req.url.startsWith(`${environment.apiUrl}/api/catalogo/public/`)
      || req.url.startsWith(`${environment.apiUrl}/api/v1/catalogo/productos`)
      || req.url.startsWith(`${environment.apiUrl}/api/v1/prendas/public/`)
      || (req.method === 'POST' && req.url === `${environment.apiUrl}/api/v1/auth`)
      || req.url.startsWith(`${environment.apiUrl}/images/`);
    if (isApi && !isPublicApi) {
      return this.auth.ensureValidToken().pipe(
        switchMap(token => token
          ? next.handle(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }))
          : next.handle(req))
      );
    }

    return next.handle(req);
  }
}
