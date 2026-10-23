import { Pipe, PipeTransform } from '@angular/core';
import { MetodoPago } from '../../core/models/models';

interface MetodoItem { value: MetodoPago; label: string; icon: string; }

@Pipe({ name: 'findMetodo' })
export class FindMetodoPipe implements PipeTransform {
  transform(metodos: MetodoItem[], value: string): string {
    const found = metodos.find(m => m.value === value);
    return found ? `${found.icon} ${found.label}` : value;
  }
}
