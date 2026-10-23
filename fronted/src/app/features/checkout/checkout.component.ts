import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom, forkJoin, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CartItem, Cliente, MetodoPago } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { ToastService } from '../../core/services/toast.service';

declare global {
    interface Window { MercadoPago: any; }
}

@Component({
    selector: 'app-checkout',
    templateUrl: './checkout.component.html',
    styleUrls: ['./checkout.component.css'],
})
export class CheckoutComponent implements OnInit, OnDestroy {
    step = 1;
    loading = false;
    transitionPhase: 'loading' | 'success' = 'loading';
    clienteForm: FormGroup;
    pagoForm: FormGroup;
    cardForm: FormGroup;
    items: CartItem[] = [];
    clienteActual: Cliente | null = null;
    metodoPago: MetodoPago = 'MERCADO_PAGO';
    pedidoCompletado: any = null;
    voucherNumero = '';
    pedidoId: number | null = null;
    private pagoConfirmado = false;
    brickReady = false;
    brickError = false;
    yapeLoading = false;
    yapeForm: FormGroup;
    private brickController: any;
    private brickRender = 0;

    readonly metodos = [
        { value: 'MERCADO_PAGO' as MetodoPago, label: 'Mercado Pago', icon: '💳', desc: 'Tarjeta en el recuadro, o Yape con celular y código' },
    ];

    constructor(
        private fb: FormBuilder,
        private cart: CartService,
        private order: OrderService,
        private toast: ToastService,
        private auth: AuthService,
        private router: Router,
        private cdr: ChangeDetectorRef,
    ) {
        this.clienteForm = this.fb.group({
            nombres: ['', [Validators.required, Validators.minLength(3)]],
            email: ['', [Validators.required, Validators.email]],
            pais: ['Perú', Validators.required],
            ciudad: ['', Validators.required],
            direccionEnvio: ['', [Validators.required, Validators.minLength(5)]],
        });
        this.pagoForm = this.fb.group({ metodoPago: ['MERCADO_PAGO', Validators.required] });
        this.cardForm = this.fb.group({ numero: [''], titular: [''], expiry: [''], cvv: [''] });
        this.yapeForm = this.fb.group({
            celular: ['', [Validators.required, Validators.pattern(/^9\d{8}$/)]],
            otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
        });
    }

    ngOnInit(): void {
        this.cart.items$.subscribe(items => this.items = items);
        const user = this.auth.currentUser;
        if (user?.email) this.clienteForm.patchValue({ email: user.email });
        if (user?.given_name) this.clienteForm.patchValue({ nombres: `${user.given_name} ${user.family_name ?? ''}`.trim() });
        const cached = this.order.getCachedCliente();
        if (cached) this.applyCliente(cached);
        else this.ponerEnvioDelRegistro();
        this.order.getCurrentCliente().subscribe({
            next: cliente => {
                this.order.cacheCurrentCliente(cliente);
                this.applyCliente(cliente);
                sessionStorage.removeItem('raymi_envio_registro');
            },
            error: () => {
                this.ponerEnvioDelRegistro();
                if (!cached && !this.clienteForm.value.ciudad) {
                    this.toast.show('Completa tus datos para crear tu perfil de cliente.', 'info');
                }
            },
        });
    }

    private ponerEnvioDelRegistro(): void {
        const raw = sessionStorage.getItem('raymi_envio_registro');
        if (!raw) return;
        try {
            const envio = JSON.parse(raw) as { pais?: string; ciudad?: string; direccionEnvio?: string };
            const actual = this.clienteForm.value;
            if (!actual.ciudad && !actual.direccionEnvio) {
                this.clienteForm.patchValue({
                    pais: envio.pais || actual.pais,
                    ciudad: envio.ciudad,
                    direccionEnvio: envio.direccionEnvio,
                });
                return;
            }
            this.clienteForm.patchValue({
                ciudad: actual.ciudad || envio.ciudad,
                direccionEnvio: actual.direccionEnvio || envio.direccionEnvio,
            });
        } catch {
            sessionStorage.removeItem('raymi_envio_registro');
        }
    }

    ngOnDestroy(): void {
        void this.destroyBrick();
    }

    private applyCliente(cliente: Cliente): void {
        this.clienteActual = cliente;
        this.clienteForm.patchValue({
            nombres: [cliente.nombres, cliente.apellidos].filter(Boolean).join(' '),
            email: cliente.email,
            pais: cliente.pais,
            ciudad: cliente.ciudad,
            direccionEnvio: cliente.direccionEnvio,
        });
    }

    get total(): number { return this.cart.total; }
    get count(): number { return this.cart.count; }
    get selectedMetodo() { return this.metodos[0]; }

    goStep(n: number): void {
        if (n !== 2) void this.destroyBrick();
        this.step = n;
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    submitCliente(): void {
        if (this.clienteForm.invalid) {
            this.clienteForm.markAllAsTouched();
            return;
        }
        if (!this.items.length) return;
        this.loading = true;
        const cliente$ = this.clienteActual ? of(this.clienteActual) : this.order.createCliente(this.clienteForm.value);
        cliente$.subscribe({
            next: cliente => {
                this.clienteActual = cliente;
                sessionStorage.removeItem('raymi_envio_registro');
                this.loading = false;
                this.step = 2;
                this.cdr.detectChanges();
                void this.renderPaymentBrick();
            },
            error: () => {
                this.loading = false;
                this.toast.show('No se pudieron guardar tus datos.', 'error');
            },
        });
    }

    private async prepararPedido(): Promise<void> {
        if (this.pedidoId) return;
        const cliente = this.clienteActual
            ?? await firstValueFrom(this.order.createCliente(this.clienteForm.value));
        this.clienteActual = cliente;
        const pedido = await firstValueFrom(this.order.createPedido({
            idCliente: cliente.idCliente!,
            fechaPedido: new Date().toISOString(),
            estadoPedido: 'PENDIENTE',
            totalPagar: this.total,
        }));
        const nuevoPedidoId = pedido.idPedido!;
        await firstValueFrom(forkJoin(this.items.map(item => this.order.addDetalle({
            idPedido: nuevoPedidoId,
            idPrenda: item.prendaId,
            cantidad: item.cantidad,
            precioUnitario: item.precio,
        }))));
        this.pedidoId = nuevoPedidoId;
    }

    private async renderPaymentBrick(): Promise<void> {
        if (!window.MercadoPago) {
            this.brickError = true;
            this.toast.show('No se pudo cargar Mercado Pago.', 'error');
            return;
        }
        const turno = ++this.brickRender;
        await this.destroyBrick();
        if (turno !== this.brickRender) return;
        this.brickReady = false;
        this.brickError = false;
        document.getElementById('paymentBrick_container')?.replaceChildren();
        const mp = new window.MercadoPago(environment.mercadoPagoPublicKey, { locale: 'es-PE' });
        const brick = await mp.bricks().create('payment', 'paymentBrick_container', {
            initialization: {
                amount: this.total,
                payer: { email: this.clienteForm.value.email },
            },
            customization: {
                visual: { style: { theme: 'default' } },
                paymentMethods: {
                    creditCard: 'all',
                    debitCard: 'all',
                },
            },
            callbacks: {
                onReady: () => {
                    this.dejarUnSoloBrick();
                    this.brickReady = true;
                    this.cdr.detectChanges();
                },
                onSubmit: ({ formData }: any) => this.submitBrickPayment(formData),
                onError: (error: any) => {
                    console.error('Mercado Pago Brick:', error);
                    this.brickError = true;
                },
            },
        });
        if (turno !== this.brickRender) {
            await brick.unmount();
            return;
        }
        this.brickController = brick;
        this.dejarUnSoloBrick();
    }

    private dejarUnSoloBrick(): void {
        const caja = document.getElementById('paymentBrick_container');
        if (!caja) return;
        while (caja.childElementCount > 1) caja.lastElementChild?.remove();
    }

    soloDigitos(event: Event, control: 'celular' | 'otp', max: number): void {
        const input = event.target as HTMLInputElement;
        const limpio = input.value.replace(/\D/g, '').slice(0, max);
        input.value = limpio;
        this.yapeForm.get(control)?.setValue(limpio, { emitEvent: false });
    }

    async pagarConYape(): Promise<void> {
        if (this.yapeForm.invalid) {
            this.yapeForm.markAllAsTouched();
            this.toast.show('Escribe el celular de 9 dígitos y el código de 6 que te muestra Yape.', 'error');
            return;
        }
        if (!window.MercadoPago) {
            this.toast.show('No se pudo cargar Mercado Pago.', 'error');
            return;
        }
        const email = this.clienteForm.value.email;
        if (!email) {
            this.toast.show('Falta el correo del pedido.', 'error');
            return;
        }
        this.yapeLoading = true;
        try {
            const mp = new window.MercadoPago(environment.mercadoPagoPublicKey, { locale: 'es-PE' });
            const yape = mp.yape({
                otp: this.yapeForm.value.otp,
                phoneNumber: this.yapeForm.value.celular,
            });
            const yapeToken = await yape.create();
            const token = yapeToken?.id;
            if (!token) {
                this.toast.show('Yape no devolvió el código de pago. Pide otro en la app e inténtalo de nuevo.', 'error');
                return;
            }
            await this.submitBrickPayment({
                token,
                transaction_amount: this.total,
                payment_method_id: 'yape',
                installments: 1,
                payer: { email },
            });
        } catch (error: any) {
            if (!(error instanceof HttpErrorResponse)) {
                const detalle = error?.message || error?.cause?.[0]?.description || 'No se pudo crear el pago con Yape.';
                this.toast.show(String(detalle), 'error');
            }
        } finally {
            this.yapeLoading = false;
            this.cdr.detectChanges();
        }
    }

    private async submitBrickPayment(formData: any): Promise<void> {
        await this.prepararPedido();
        if (!this.pedidoId) throw new Error('Pedido no preparado');
        try {
            const response = await firstValueFrom(this.order.crearPagoMercadoPago({
                pedidoId: this.pedidoId,
                token: formData.token,
                transaction_amount: Number(formData.transaction_amount || this.total),
                payment_method_id: formData.payment_method_id,
                issuer_id: formData.issuer_id == null ? undefined : String(formData.issuer_id),
                installments: Number(formData.installments || 1),
                device_id: formData.device_id || (window as any).MP_DEVICE_SESSION_ID,
                payer: {
                    email: formData.payer?.email || this.clienteForm.value.email,
                    identification: {
                        type: formData.payer?.identification?.type,
                        number: formData.payer?.identification?.number,
                    },
                },
            }));
            console.info('Respuesta de Mercado Pago:', response);
            if (response.status === 'rejected') {
                this.pagoConfirmado = true;
                this.toast.show(this.mensajeRechazo(response.status_detail), 'error');
                await this.router.navigate(['/mis-pedidos']);
                return;
            }
            this.cart.clearCart();
            this.pagoConfirmado = response.status === 'approved' || response.status === 'pending' || response.status === 'in_process';
            if (response.status === 'approved') {
                await this.router.navigate(['/pedido-exitoso', this.pedidoId]);
                return;
            }
            await this.router.navigate(['/checkout/pending'], { queryParams: { payment_id: response.paymentId } });
        } catch (error) {
            console.error('Error real al procesar el pago con Mercado Pago:', error);
            const httpError = error as HttpErrorResponse;
            const backend = httpError.error;
            const detail = backend?.error || backend?.message || httpError.message || 'Error desconocido';
            this.toast.show(`Mercado Pago: ${detail}`, 'error');
            throw error;
        }
    }

    private mensajeRechazo(detalle?: string): string {
        const codigo = (detalle || '').toLowerCase();
        if (codigo.includes('high_risk') || codigo.includes('blacklist')) {
            return 'Mercado Pago rechazó la tarjeta por su control de seguridad. Prueba con otra tarjeta o con Yape.';
        }
        if (codigo.includes('insufficient')) {
            return 'La tarjeta no tiene saldo suficiente.';
        }
        if (codigo.includes('security_code') || codigo.includes('bad_filled')) {
            return 'Revisa el número, la fecha o el código de la tarjeta.';
        }
        return 'Mercado Pago no aprobó el pago. Prueba con otro medio.';
    }

    private async destroyBrick(): Promise<void> {
        if (this.brickController) {
            await this.brickController.unmount();
            this.brickController = null;
        }
        document.getElementById('paymentBrick_container')?.replaceChildren();
    }

    goToReview(): void {}
    confirmOrder(): void {}
    hasError(form: FormGroup, field: string, error: string): boolean {
        const ctrl = form.get(field);
        return !!(ctrl && ctrl.touched && ctrl.hasError(error));
    }
    get qrImageUrl(): string { return ''; }
    formatCardNumber(_event: Event): void {}
    formatExpiry(_event: Event): void {}
    getCardBrand(): string { return ''; }
}
