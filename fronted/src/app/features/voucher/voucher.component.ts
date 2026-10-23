import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';

/**
 * VoucherComponent — vista dedicada exclusiva del comprobante de pago.
 * Ruta: /voucher/:id
 *
 * FLUJO COMPLETO:
 *   checkout.component.ts → step 4 (procesando 3-5s) → router.navigate(['/voucher']) → VoucherComponent
 *
 * Recibe los datos del pedido via state del router:
 *   this.router.navigate(['/voucher', idPedido], { state: { pedido: pedidoData } })
 *
 * Ubicar en: src/app/features/voucher/voucher.component.ts
 */
@Component({
    selector: 'app-voucher',
    templateUrl: './voucher.component.html',
    styleUrls: ['./voucher.component.css'],
})
export class VoucherComponent implements OnInit {
    pedido: any = null;
    voucherNumero = '';
    loading = true;

    readonly metodoLabels: Record<string, string> = {
        YAPE: '📲 Yape / Plin',
        TARJETA: '💳 Tarjeta débito/crédito',
        TRANSFERENCIA: '🏦 Transferencia bancaria',
        EFECTIVO: '💵 Pago contra entrega',
        STRIPE: '💳 Tarjeta mediante Stripe',
    };

    constructor(private route: ActivatedRoute, private router: Router) {}

    ngOnInit(): void {
        // Leer estado pasado por el router desde checkout
        const navigation = this.router.getCurrentNavigation();
        const state = navigation?.extras?.state as { pedido: any } | undefined;

        if (state?.pedido) {
            this.pedido = state.pedido;
            this.voucherNumero = `PAC-${new Date().getFullYear()}-${String(this.pedido.idPedido).padStart(5, '0')}`;
            this.loading = false;
            return;
        }

        // Fallback: leer desde sessionStorage (por si el usuario refresca la página)
        const saved = sessionStorage.getItem('raymisupreme_last_voucher');
        if (saved) {
            try {
                this.pedido = JSON.parse(saved);
                this.voucherNumero = `PAC-${new Date().getFullYear()}-${String(this.pedido.idPedido).padStart(5, '0')}`;
            } catch {}
        }

        this.loading = false;

        // Si no hay datos, redirigir al inicio
        if (!this.pedido) {
            setTimeout(() => this.router.navigate(['/']), 2000);
        }
    }

    getMetodoLabel(metodo: string): string {
        return this.metodoLabels[metodo] || metodo;
    }

    printVoucher(): void {
        window.print();
    }

    async downloadPdf(): Promise<void> {
        const documentElement = document.getElementById('voucher-print');
        if (!documentElement) return;

        const canvas = await html2canvas(documentElement, {
            scale: 2,
            backgroundColor: '#ffffff',
            useCORS: true,
        });
        const pdf = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });
        const pageWidth = pdf.internal.pageSize.getWidth();
        const pageHeight = pdf.internal.pageSize.getHeight();
        const margin = 8;
        const imageWidth = pageWidth - margin * 2;
        const imageHeight = canvas.height * imageWidth / canvas.width;
        const image = canvas.toDataURL('image/png');

        let remainingHeight = imageHeight;
        let position = margin;
        pdf.addImage(image, 'PNG', margin, position, imageWidth, imageHeight);
        remainingHeight -= pageHeight - margin * 2;
        while (remainingHeight > 0) {
            position = margin - (imageHeight - remainingHeight);
            pdf.addPage();
            pdf.addImage(image, 'PNG', margin, position, imageWidth, imageHeight);
            remainingHeight -= pageHeight - margin * 2;
        }
        pdf.save(`boleta-${this.voucherNumero}.pdf`);
    }

    volverAlInicio(): void {
        sessionStorage.removeItem('raymisupreme_last_voucher');
        this.router.navigate(['/']);
    }

    verMisPedidos(): void {
        sessionStorage.removeItem('raymisupreme_last_voucher');
        this.router.navigate(['/mis-pedidos']);
    }

    seguirComprando(): void {
        this.router.navigate(['/catalogo']);
    }
}
