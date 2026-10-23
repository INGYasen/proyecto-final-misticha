import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AuthService } from './auth.service';

export interface FavoriteItem {
  prendaId: number;
  nombrePrenda: string;
  precio: number;
  urlFoto?: string;
  talla?: string;
  color?: string;
}

@Injectable({ providedIn: 'root' })
export class FavoriteService {
  private readonly _items$ = new BehaviorSubject<FavoriteItem[]>([]);
  readonly items$ = this._items$.asObservable();

  private readonly _open$ = new BehaviorSubject(false);
  readonly open$ = this._open$.asObservable();

  constructor(private auth: AuthService) {
    this.auth.user$.subscribe(user => this.activate(user?.sub));
    this.activate(this.auth.currentUser?.sub);
  }

  get items(): FavoriteItem[] { return this._items$.value; }
  get count(): number { return this.items.length; }

  isSaved(prendaId: number): boolean {
    return this.items.some(item => item.prendaId === prendaId);
  }

  toggle(item: FavoriteItem): boolean {
    if (this.isSaved(item.prendaId)) {
      this.update(this.items.filter(current => current.prendaId !== item.prendaId));
      return false;
    }
    this.update([item, ...this.items]);
    return true;
  }

  remove(prendaId: number): void {
    this.update(this.items.filter(item => item.prendaId !== prendaId));
  }

  open(): void { this._open$.next(true); }
  close(): void { this._open$.next(false); }
  togglePanel(): void { this._open$.next(!this._open$.value); }

  private activate(sub?: string): void {
    const owner = sub || 'invitado';
    if (sub) this.mergeGuest(sub);
    this._items$.next(this.read(owner));
  }

  private mergeGuest(sub: string): void {
    const guest = this.read('invitado');
    if (!guest.length) return;
    const own = this.read(sub);
    const merged = [...own];
    guest.forEach(item => {
      if (!merged.some(current => current.prendaId === item.prendaId)) merged.unshift(item);
    });
    this.write(sub, merged);
    this.write('invitado', []);
  }

  private update(items: FavoriteItem[]): void {
    const owner = this.auth.currentUser?.sub || 'invitado';
    this.write(owner, items);
    this._items$.next(items);
  }

  private key(owner: string): string {
    return `raymisupreme_favoritos_${owner}`;
  }

  private read(owner: string): FavoriteItem[] {
    try {
      const raw = localStorage.getItem(this.key(owner));
      const parsed = raw ? JSON.parse(raw) : [];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }

  private write(owner: string, items: FavoriteItem[]): void {
    localStorage.setItem(this.key(owner), JSON.stringify(items));
  }
}
