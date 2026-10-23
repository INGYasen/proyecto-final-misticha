import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, finalize, shareReplay, switchMap, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { LoginResponse, UserInfo } from '../models/models';

const CLAVE_VERIFIER = 'raymi_pkce_verifier';
const CLAVE_STATE = 'raymi_pkce_state';
const CLAVE_DESTINO = 'raymi_pkce_destino';
const CLAVE_PAGAR = 'raymi_pagar_despues';
const CLAVE_VUELTA = 'raymisupreme_auth_return_url';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private idToken: string | null = null;
  private refreshInFlight: Observable<string> | null = null;

  private readonly tokenUrl =
    `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/token`;
  private readonly authUrl =
    `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/auth`;

  private _isLoggedIn$ = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this._isLoggedIn$.asObservable();

  private _user$ = new BehaviorSubject<UserInfo | null>(null);
  user$ = this._user$.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  /** Lo marcó quien apretó "Ir al pago" con algo en el carrito. */
  marcarQuieroPagar(): void {
    sessionStorage.setItem(CLAVE_PAGAR, '1');
  }

  /** Entrar o registrarse desde el menú, sin venir del pago. */
  olvidarQuieroPagar(): void {
    sessionStorage.removeItem(CLAVE_PAGAR);
    const vuelta = sessionStorage.getItem(CLAVE_VUELTA) || '';
    if (vuelta.startsWith('/checkout')) sessionStorage.removeItem(CLAVE_VUELTA);
  }

  destinoDespuesDeEntrar(): string {
    const quierePagar = sessionStorage.getItem(CLAVE_PAGAR) === '1';
    const guardado = sessionStorage.getItem(CLAVE_VUELTA) || '/';
    sessionStorage.removeItem(CLAVE_VUELTA);
    if (quierePagar) return '/checkout';
    if (guardado.startsWith('/checkout')) return '/';
    return guardado;
  }

  /** Redirige a Keycloak. La contraseña no se escribe en la tienda. */
  async iniciarSesion(destino = '/'): Promise<void> {
    const verifier = this.aleatorio(64);
    const state = this.aleatorio(32);
    const challenge = await this.challenge(verifier);
    sessionStorage.setItem(CLAVE_VERIFIER, verifier);
    sessionStorage.setItem(CLAVE_STATE, state);
    sessionStorage.setItem(CLAVE_DESTINO, destino);
    const url = new URL(this.authUrl);
    url.searchParams.set('client_id', environment.keycloak.clientId);
    url.searchParams.set('redirect_uri', environment.keycloak.redirectUri);
    url.searchParams.set('response_type', 'code');
    url.searchParams.set('scope', 'openid profile email');
    url.searchParams.set('state', state);
    url.searchParams.set('code_challenge', challenge);
    url.searchParams.set('code_challenge_method', 'S256');
    url.searchParams.set('prompt', 'login');
    window.location.assign(url.toString());
  }

  completarSiVuelve(): Observable<boolean> {
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const state = params.get('state');
    if (!code && !state) return of(false);
    const esperado = sessionStorage.getItem(CLAVE_STATE);
    const verifier = sessionStorage.getItem(CLAVE_VERIFIER);
    sessionStorage.removeItem(CLAVE_STATE);
    sessionStorage.removeItem(CLAVE_VERIFIER);
    if (!code || !verifier || !esperado || state !== esperado) {
      return throwError(() => new Error('state o code_verifier invalido'));
    }
    const body = new HttpParams()
      .set('grant_type', 'authorization_code')
      .set('client_id', environment.keycloak.clientId)
      .set('code', code)
      .set('redirect_uri', environment.keycloak.redirectUri)
      .set('code_verifier', verifier);
    return this.http.post<LoginResponse>(this.tokenUrl, body.toString(), {
      headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }),
    }).pipe(
      tap(res => this.guardarEnMemoria(res)),
      tap(() => {
        const destino = sessionStorage.getItem(CLAVE_DESTINO) || '/';
        sessionStorage.removeItem(CLAVE_DESTINO);
        this.router.navigateByUrl(destino);
      }),
      switchMap(() => of(true)),
    );
  }

  ensureValidToken(): Observable<string | null> {
    if (!this.accessToken) return of(null);
    if (this.expiry(this.accessToken) - Date.now() > 20_000) return of(this.accessToken);
    return this.refresh().pipe(catchError(() => {
      this.clearSession();
      return of(null);
    }));
  }

  logout(): void {
    const idToken = this.idToken;
    this.clearSession();
    const url = new URL(`${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/logout`);
    url.searchParams.set('client_id', environment.keycloak.clientId);
    url.searchParams.set('post_logout_redirect_uri', 'http://localhost:4200/');
    if (idToken) url.searchParams.set('id_token_hint', idToken);
    window.location.assign(url.toString());
  }

  getToken(): string | null {
    if (!this.accessToken || this.expiry(this.accessToken) <= Date.now()) return null;
    return this.accessToken;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  get currentUser(): UserInfo | null {
    return this._user$.value;
  }

  hasRole(role: string): boolean {
    return this._user$.value?.realm_access?.roles?.includes(role) ?? false;
  }

  private refresh(): Observable<string> {
    if (this.refreshInFlight) return this.refreshInFlight;
    if (!this.refreshToken) return throwError(() => new Error('sin refresh'));
    const body = new HttpParams()
      .set('grant_type', 'refresh_token')
      .set('client_id', environment.keycloak.clientId)
      .set('refresh_token', this.refreshToken);
    this.refreshInFlight = this.http.post<LoginResponse>(this.tokenUrl, body.toString(), {
      headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }),
    }).pipe(
      tap(res => this.guardarEnMemoria(res)),
      switchMap(res => of(res.access_token)),
      finalize(() => { this.refreshInFlight = null; }),
      shareReplay(1),
    );
    return this.refreshInFlight;
  }

  private guardarEnMemoria(res: LoginResponse): void {
    this.accessToken = res.access_token;
    this.refreshToken = res.refresh_token || null;
    this.idToken = res.id_token || this.idToken;
    localStorage.removeItem('raymisupreme_token');
    localStorage.removeItem('raymisupreme_refresh');
    localStorage.removeItem('raymisupreme_user');
    try {
      this._user$.next(JSON.parse(this.decode(res.access_token)) as UserInfo);
    } catch {
      this._user$.next(null);
    }
    this._isLoggedIn$.next(true);
  }

  private clearSession(): void {
    this.accessToken = null;
    this.refreshToken = null;
    this.idToken = null;
    localStorage.removeItem('raymisupreme_token');
    localStorage.removeItem('raymisupreme_refresh');
    localStorage.removeItem('raymisupreme_user');
    this._isLoggedIn$.next(false);
    this._user$.next(null);
  }

  private async challenge(verifier: string): Promise<string> {
    const data = new TextEncoder().encode(verifier);
    const hash = await crypto.subtle.digest('SHA-256', data);
    return this.base64url(new Uint8Array(hash));
  }

  private aleatorio(bytes: number): string {
    const buffer = new Uint8Array(bytes);
    crypto.getRandomValues(buffer);
    return this.base64url(buffer);
  }

  private base64url(bytes: Uint8Array): string {
    let text = '';
    bytes.forEach(b => text += String.fromCharCode(b));
    return btoa(text).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
  }

  private expiry(token: string): number {
    try {
      const payload = JSON.parse(this.decode(token));
      return (payload.exp || 0) * 1000;
    } catch {
      return 0;
    }
  }

  private decode(token: string): string {
    const part = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return decodeURIComponent(atob(part).split('').map(c =>
      '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join(''));
  }
}
