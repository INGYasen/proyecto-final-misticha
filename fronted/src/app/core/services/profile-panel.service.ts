import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProfilePanelService {
  private readonly _open$ = new BehaviorSubject(false);
  readonly open$ = this._open$.asObservable();

  open(): void { this._open$.next(true); }
  close(): void { this._open$.next(false); }
  toggle(): void { this._open$.next(!this._open$.value); }
}
