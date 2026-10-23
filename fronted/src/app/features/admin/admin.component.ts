import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../core/services/toast.service';
import { CommonModule, DatePipe, DecimalPipe, CurrencyPipe } from '@angular/common';
import { Descuento, DescuentoService } from '../../core/services/descuento.service';
import { AuthService } from '../../core/services/auth.service';
import { catchError, of } from 'rxjs';

export type AdminView = 'dashboard' | 'proveedores' | 'lotes' | 'prendas' | 'catalogo' | 'ordenes' | 'pagos' | 'clientes' | 'descuentos' | 'usuarios';
export type PrendasStep = 'list' | 'create' | 'precio' | 'publish';

@Component({
    selector: 'app-admin',
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.css'],
    imports: [CommonModule, DecimalPipe, DatePipe, ReactiveFormsModule],
    standalone: true,
})
export class AdminComponent implements OnInit {
    private readonly maxImageSize = 10 * 1024 * 1024;
    activeView: AdminView = 'dashboard';
    soloLectura = false;
    sidebarCollapsed = false;
    base = environment.apiUrl;

    today = new Date().toLocaleDateString('es-PE', {
        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric',
    });

    metrics = {
        totalVentas: 0,
        pedidosHoy: 0,
        stockTotal: 0,
        artesanos: 0,
        lotesTotal: 0,
        pagosConfirmados: 0,
        productosPublicados: 0,
        ingresosSemanales: [0, 0, 0, 0, 0, 0, 0],
        pedidosPorEstado: { PENDIENTE: 0, PAGADO: 0, CANCELADO: 0, CONFIRMADO: 0, ENVIADO: 0, ENTREGADO: 0 } as Record<string, number>,
    };

    // ─── Proveedores ──────────────────────────────────────────────
    proveedores: any[] = [];
    proveedorForm!: FormGroup;
    editingProveedor: any = null;
    showProveedorModal = false;

    // ─── Lotes ────────────────────────────────────────────────────
    lotes: any[] = [];
    loteForm!: FormGroup;
    editingLote: any = null;
    showLoteModal = false;

    // ─── Prendas — 3-step workflow ────────────────────────────────
    prendas: any[] = [];
    catalogoItems: any[] = [];   // items ya publicados en catálogo
    categorias: any[] = [];
    loading = false;
    systemAlert: string | null = null;
    cuentaAbierta = false;

    // Step state
    prendasStep: PrendasStep = 'list';
    selectedPrenda: any = null;   // prenda en el flujo actual

    // Step 1 — Crear prenda
    prendaForm!: FormGroup;
    editingPrenda: any = null;
    selectedImageFile: File | null = null;
    imagePreview: string | null = null;

    // Step 2 — Asignar precio
    precioForm!: FormGroup;
    precioExistente: any = null;

    // Step 3 — Publicar al catálogo
    publishForm!: FormGroup;

    // ─── Órdenes ──────────────────────────────────────────────────
    ordenes: any[] = [];
    ordenesRecientes: any[] = [];
    selectedOrden: any = null;
    showOrdenModal = false;
    pagos: any[] = [];
    clientes: any[] = [];
    usuarios: any[] = [];

    // ─── Descuentos ───────────────────────────────────────────────
    descuentos: Descuento[] = [];
    preciosCatalogo: any[] = [];
    descuentoForm!: FormGroup;
    editingDescuento: Descuento | null = null;
    showDescuentoModal = false;

    readonly chartDays = ['L', 'M', 'X', 'J', 'V', 'S', 'D'];

    constructor(
        private http: HttpClient,
        private fb: FormBuilder,
        private toast: ToastService,
        private descuentoSvc: DescuentoService,
        public auth: AuthService,
        private route: ActivatedRoute,
        private router: Router,
    ) {}

    ngOnInit(): void {
        this.soloLectura = this.route.snapshot.data['panel'] === 'vendedora';
        this.initForms();
        this.loadDashboard();
    }

    initForms(): void {
        this.proveedorForm = this.fb.group({
            nombres: ['', [Validators.required, Validators.minLength(3)]],
            apellidos: ['', Validators.required],
            dni: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
            telefono: ['', Validators.required],
            comunidadOZona: ['', Validators.required],
            region: ['', Validators.required],
        });

        this.loteForm = this.fb.group({
            idArtesano: ['', Validators.required],
            fechaCompra: ['', Validators.required],
            cantidadTotalRecibida: ['', [Validators.required, Validators.min(1)]],
            precioTotalPagado: ['', [Validators.required, Validators.min(0)]],
            descripcionGeneral: [''],
            estadoLote: ['RECIBIDO'],
        });

        this.prendaForm = this.fb.group({
            idLote: ['', Validators.required],
            idCategoria: ['', Validators.required],
            publico: ['UNISEX', Validators.required],
            codigoUnico: ['', Validators.required],
            nombrePrenda: ['', Validators.required],
            talla: ['', Validators.required],
            color: ['', Validators.required],
            material: ['Lana de alpaca'],
            stock: ['', [Validators.required, Validators.min(0)]],
        });

        this.precioForm = this.fb.group({
            nombre: ['', Validators.required],
            precioBase: ['', [Validators.required, Validators.min(0.01)]],
        });

        this.publishForm = this.fb.group({
            descripcionPublica: ['', Validators.required],
            urlFoto: [''],
            destacado: [false],
        });

        this.descuentoForm = this.fb.group({
            codigo: ['', Validators.required],
            descripcion: [''],
            porcentaje: ['', [Validators.required, Validators.min(1), Validators.max(99)]],
            fechaInicio: ['', Validators.required],
            fechaFin: ['', Validators.required],
            prendaIds: [[] as number[]],
        });
    }

    // ── NAVIGATION ────────────────────────────────────────────────

    setView(view: AdminView): void {
        if (this.soloLectura && (view === 'pagos' || view === 'usuarios')) {
            this.toast.show('Pagos y usuarios son solo del administrador', 'error');
            return;
        }
        this.activeView = view;
        this.prendasStep = 'list';
        switch (view) {
            case 'dashboard': this.loadDashboard(); break;
            case 'proveedores': this.loadProveedores(); break;
            case 'lotes': this.loadLotes(); break;
            case 'prendas': this.loadPrendas(); break;
            case 'catalogo': this.loadCatalogo(); break;
            case 'ordenes': this.loadOrdenes(); break;
            case 'pagos': this.loadPagos(); break;
            case 'clientes': this.loadClientes(); break;
            case 'descuentos': this.loadDescuentos(); break;
            case 'usuarios': this.loadUsuarios(); break;
        }
    }

    showSystemAlert(context: string): void {
        this.systemAlert = `Servicio no disponible: ${context}. El backend está aplicando su mecanismo de resiliencia.`;
    }

    dismissSystemAlert(): void { this.systemAlert = null; }

    get adminName(): string {
        const user = this.auth.currentUser;
        return [user?.given_name, user?.family_name].filter(Boolean).join(' ') || user?.email || user?.sub || 'Administrador';
    }

    get adminInitial(): string { return this.adminName.charAt(0).toUpperCase(); }

    get adminRole(): string {
        const roles = this.auth.currentUser?.realm_access?.roles || [];
        if (this.soloLectura || roles.includes('VENDEDORA')) return 'Vendedora';
        return roles.includes('SUPER_ADMIN') ? 'Super Admin' : roles.includes('ADMIN') ? 'Administrador' : (roles[0] || 'Usuario');
    }

    toggleSidebar(): void { this.sidebarCollapsed = !this.sidebarCollapsed; }

    toggleCuenta(event: Event): void {
        event.stopPropagation();
        this.cuentaAbierta = !this.cuentaAbierta;
    }

    irATienda(): void {
        this.cuentaAbierta = false;
        this.router.navigate(['/']);
    }

    cerrarSesion(): void {
        this.cuentaAbierta = false;
        this.auth.logout();
    }

    // ── DASHBOARD ─────────────────────────────────────────────────

    loadDashboard(): void {
        this.loading = true;
        this.http.get<any[]>(`${this.base}/api/v1/pedidos`).subscribe({
            next: pedidos => {
                this.ordenesRecientes = [...pedidos]
                    .sort((a, b) => new Date(b.fechaPedido).getTime() - new Date(a.fechaPedido).getTime())
                    .slice(0, 4);
                const today = new Date().toDateString();
                this.metrics.pedidosHoy = pedidos.filter(p =>
                    new Date(p.fechaPedido).toDateString() === today
                ).length;
                this.metrics.pedidosPorEstado = { PENDIENTE: 0, PAGADO: 0, CANCELADO: 0, CONFIRMADO: 0, ENVIADO: 0, ENTREGADO: 0 };
                pedidos.forEach(p => {
                    const estado = p.estadoPedido || 'PENDIENTE';
                    this.metrics.pedidosPorEstado[estado] = (this.metrics.pedidosPorEstado[estado] || 0) + 1;
                });
                this.aplicarVentas(pedidos);
                this.loading = false;
            },
            error: () => {
                this.showSystemAlert('pedidos y ventas');
                this.loading = false;
            },
        });

        this.http.get<any[]>(`${this.base}/api/v1/artesanos`).subscribe({
            next: a => this.metrics.artesanos = a.length,
            error: () => this.showSystemAlert('artesanos'),
        });

        this.http.get<any[]>(`${this.base}/api/v1/prendas`).subscribe({
            next: p => this.metrics.stockTotal = p.reduce((s, x) => s + (x.stock || 0), 0),
            error: () => this.showSystemAlert('inventario'),
        });

        this.loadPaymentMetrics();
        this.loadPublishedProductsMetric();
    }

    get chartMax(): number {
        return Math.max(...this.metrics.ingresosSemanales, 1);
    }

    chartBarHeight(val: number): number {
        return Math.round((val / this.chartMax) * 100);
    }

    getTotalPedidos(): number {
        return Object.values(this.metrics.pedidosPorEstado).reduce((sum, n) => sum + n, 0);
    }

    private aplicarVentas(pedidos: any[]): void {
        const cobrados = pedidos.filter(p =>
            ['PAGADO', 'CONFIRMADO', 'ENVIADO', 'ENTREGADO'].includes(p.estadoPedido)
        );
        this.metrics.totalVentas = cobrados.reduce((sum, p) => sum + Number(p.totalPagar || 0), 0);
        const days = Array.from({ length: 7 }, (_, i) => {
            const d = new Date();
            d.setDate(d.getDate() - (6 - i));
            return d.toDateString();
        });
        this.metrics.ingresosSemanales = days.map(day => cobrados
            .filter(p => new Date(p.fechaPedido).toDateString() === day)
            .reduce((sum, p) => sum + Number(p.totalPagar || 0), 0));
    }

    // ── PROVEEDORES ───────────────────────────────────────────────

    loadProveedores(): void {
        this.http.get<any[]>(`${this.base}/api/v1/artesanos`).subscribe({
            next: d => this.proveedores = d,
            error: () => { this.proveedores = []; this.showSystemAlert('artesanos'); },
        });
    }

    openProveedorModal(item?: any): void {
        this.editingProveedor = item || null;
        this.proveedorForm.reset();
        if (item) this.proveedorForm.patchValue(item);
        this.showProveedorModal = true;
    }

    closeProveedorModal(): void { this.showProveedorModal = false; }

    saveProveedor(): void {
        if (this.proveedorForm.invalid) { this.proveedorForm.markAllAsTouched(); return; }
        const data = this.proveedorForm.value;
        const req = this.editingProveedor
            ? this.http.put(`${this.base}/api/v1/artesanos/${this.editingProveedor.idArtesano}`, data)
            : this.http.post(`${this.base}/api/v1/artesanos`, data);
        req.subscribe({
            next: () => {
                this.toast.show(this.editingProveedor ? 'Proveedor actualizado ✓' : 'Proveedor registrado ✓');
                this.closeProveedorModal();
                this.loadProveedores();
            },
            error: () => this.showSystemAlert('guardado de artesanos'),
        });
    }

    deleteProveedor(id: number): void {
        if (!confirm('¿Eliminar este proveedor?')) return;
        this.http.delete(`${this.base}/api/v1/artesanos/${id}`).subscribe({
            next: () => { this.toast.show('Eliminado ✓'); this.loadProveedores(); },
            error: () => this.showSystemAlert('eliminación de artesanos'),
        });
    }

    // ── LOTES ─────────────────────────────────────────────────────

    loadLotes(): void {
        this.http.get<any[]>(`${this.base}/api/v1/lotes`).subscribe({
            next: d => this.lotes = d,
            error: () => { this.lotes = []; this.showSystemAlert('lotes de acopio'); },
        });
        if (!this.proveedores.length) this.loadProveedores();
    }

    openLoteModal(item?: any): void {
        this.editingLote = item || null;
        this.loteForm.reset({ estadoLote: 'RECIBIDO' });
        if (item) this.loteForm.patchValue(item);
        this.showLoteModal = true;
    }

    closeLoteModal(): void { this.showLoteModal = false; }

    saveLote(): void {
        if (this.loteForm.invalid) { this.loteForm.markAllAsTouched(); return; }
        const data = this.loteForm.value;
        const req = this.editingLote
            ? this.http.put(`${this.base}/api/v1/lotes/${this.editingLote.idLote}`, data)
            : this.http.post(`${this.base}/api/v1/lotes`, data);
        req.subscribe({
            next: () => {
                this.toast.show(this.editingLote ? 'Lote actualizado ✓' : 'Lote registrado ✓');
                this.closeLoteModal();
                this.loadLotes();
            },
            error: () => this.showSystemAlert('guardado de lotes'),
        });
    }

    deleteLote(id: number): void {
        if (!confirm('¿Eliminar este lote?')) return;
        this.http.delete(`${this.base}/api/v1/lotes/${id}`).subscribe({
            next: () => { this.toast.show('Eliminado ✓'); this.loadLotes(); },
            error: () => this.showSystemAlert('eliminación de lotes'),
        });
    }

    getArtesanoNombre(idArtesano: number): string {
        const p = this.proveedores.find((a: any) => a.idArtesano === idArtesano);
        return p ? `${p.nombres ?? p.nombre} ${p.apellidos ?? p.apellido}` : `Artesano #${idArtesano}`;
    }

    // ── PRENDAS — 3-STEP WORKFLOW ─────────────────────────────────

    loadPrendas(): void {
        this.http.get<any[]>(`${this.base}/api/v1/prendas`).subscribe({
            next: d => this.prendas = d,
            error: () => { this.prendas = []; this.showSystemAlert('inventario de prendas'); },
        });
        this.http.get<any[]>(`${this.base}/api/v1/catalogo/items`).subscribe({
            next: items => this.catalogoItems = items,
            error: () => { this.catalogoItems = []; this.showSystemAlert('catálogo'); },
        });

        this.http.get<any[]>(`${this.base}/api/v1/lotes`).subscribe({
            next: lotes => this.metrics.lotesTotal = lotes.length,
            error: () => this.showSystemAlert('lotes de acopio'),
        });

        this.loadPaymentMetrics();
        this.loadPublishedProductsMetric();

        this.http.get<any[]>(`${this.base}/api/v1/categorias`).subscribe({
            next: d => this.categorias = d,
            error: () => { this.categorias = []; this.showSystemAlert('categorías de inventario'); },
        });
        if (!this.lotes.length) this.loadLotes();
    }

    private loadPaymentMetrics(): void {
        if (this.soloLectura) return;
        this.http.get<any[]>(`${this.base}/api/v1/pagos`).subscribe({
            next: pagos => {
                const ok = ['CONFIRMADO', 'APROBADO', 'EXITOSO', 'PAGADO'];
                this.metrics.pagosConfirmados = pagos.filter(p =>
                    ok.includes(String(p.estado || '').toUpperCase())
                ).length;
            },
            error: () => this.showSystemAlert('pagos'),
        });
    }

    private loadPublishedProductsMetric(): void {
        this.http.get<any[]>(`${this.base}/api/v1/catalogo/items`).subscribe({
            next: items => this.metrics.productosPublicados = items.filter(i => i.activo !== false).length,
            error: () => this.showSystemAlert('catálogo'),
        });
    }

    isPublicada(prenda: any): boolean {
        return this.catalogoItems.some(i => i.idPrenda === prenda.idPrenda && i.activo !== false);
    }

    getPrendaFoto(prenda: any): string | null {
        const item = this.catalogoItems.find(i => i.idPrenda === prenda.idPrenda);
        return prenda?.urlFoto || item?.urlFoto || null;
    }

    getCategoriaNombre(idCategoria: number): string {
        const c = this.categorias.find(x => x.idCategoria === idCategoria || x.id === idCategoria);
        return c?.nombre ?? `Cat. ${idCategoria}`;
    }

    // ── Step 1: Crear / editar prenda ──

    openCrearPrenda(item?: any): void {
        this.editingPrenda = item || null;
        this.prendaForm.reset({ material: 'Lana de alpaca', publico: 'UNISEX' });
        if (item) this.prendaForm.patchValue(item);
        this.selectedImageFile = null;
        this.imagePreview = item ? this.getPrendaFoto(item) : null;
        this.prendasStep = 'create';
    }

    onImageSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (!input.files?.length) return;
        const file = input.files[0];
        if (file.size > this.maxImageSize) {
            input.value = '';
            this.selectedImageFile = null;
            this.imagePreview = null;
            this.toast.show('La imagen supera el limite de 10 MB.', 'error');
            return;
        }
        this.selectedImageFile = file;
        const reader = new FileReader();
        reader.onload = (e) => this.imagePreview = e.target?.result as string;
        reader.readAsDataURL(this.selectedImageFile);
    }

    savePrenda(): void {
        if (this.prendaForm.invalid) { this.prendaForm.markAllAsTouched(); return; }

        if (this.selectedImageFile) {
            const formData = new FormData();
            formData.append('imagen', this.selectedImageFile, this.selectedImageFile.name);
            this.http.post<any>(`${this.base}/api/v1/imagenes/prendas`, formData).subscribe({
                next: (res) => this.savePrendaWithUrl(res.url),
                error: (error) => this.showImageUploadError(error),
            });
        } else {
            this.savePrendaWithUrl(this.imagePreview || undefined);
        }
    }

    private savePrendaWithUrl(urlFoto?: string): void {
        const data = {
            ...this.prendaForm.value,
            urlFoto: urlFoto || this.editingPrenda?.urlFoto || null,
        };

        const isEdit = !!this.editingPrenda;
        const url = isEdit
            ? `${this.base}/api/v1/prendas/${this.editingPrenda.idPrenda}`
            : `${this.base}/api/v1/prendas`;

        const req = isEdit ? this.http.put<any>(url, data) : this.http.post<any>(url, data);

        req.subscribe({
            next: (prenda) => {
                this.toast.show(isEdit ? 'Prenda actualizada ✓' : 'Prenda creada ✓');
                const catalogItem = isEdit
                    ? this.catalogoItems.find(i => i.idPrenda === this.editingPrenda.idPrenda)
                    : null;
                if (catalogItem && urlFoto) {
                    this.http.put(
                        `${this.base}/api/v1/catalogo/items/${catalogItem.idItem ?? catalogItem.id}`,
                        { ...catalogItem, urlFoto }
                    ).subscribe({
                        next: () => catalogItem.urlFoto = urlFoto,
                        error: () => this.toast.show('La prenda se guardó, pero no se pudo actualizar su foto en la tienda.', 'error'),
                    });
                }
                // After creating, move to step 2 (precio)
                this.selectedPrenda = {
                    ...data,
                    ...(prenda ?? { idPrenda: this.editingPrenda?.idPrenda }),
                    urlFoto: urlFoto || this.imagePreview || '',
                };
                if (!isEdit) {
                    // Proceed to price step automatically
                    this.openAsignarPrecio(this.selectedPrenda);
                } else {
                    this.prendasStep = 'list';
                    this.loadPrendas();
                }
            },
            error: () => this.showSystemAlert('guardado de prendas'),
        });
    }

    // ── Step 2: Asignar precio ──

    openAsignarPrecio(prenda: any): void {
        this.selectedPrenda = prenda;
        this.precioForm.reset({ nombre: prenda.nombrePrenda || '' });
        // Check if price already exists
        this.http.get<any>(`${this.base}/api/v1/precios/prendas/${prenda.idPrenda}`).pipe(
            catchError(() => of(null))
        ).subscribe(precio => {
            this.precioExistente = precio;
            if (precio) {
                this.precioForm.patchValue({ nombre: precio.nombre, precioBase: precio.precioBase });
            }
            this.prendasStep = 'precio';
        });
    }

    savePrecio(): void {
        if (this.precioForm.invalid) { this.precioForm.markAllAsTouched(); return; }
        const data = {
            ...this.precioForm.value,
            prendaId: this.selectedPrenda.idPrenda,
        };

        const req = this.precioExistente
            ? this.http.put(`${this.base}/api/v1/precios/prendas/${this.precioExistente.id}`, data)
            : this.http.post(`${this.base}/api/v1/precios/prendas`, data);

        req.subscribe({
            next: () => {
                this.toast.show('Precio guardado ✓');
                this.openPublicar(this.selectedPrenda);
            },
            error: () => this.showSystemAlert('guardado de precios'),
        });
    }

    skipPrecio(): void {
        this.openPublicar(this.selectedPrenda);
    }

    // ── Step 3: Publicar al catálogo ──

    openPublicar(prenda: any): void {
        this.selectedPrenda = prenda;
        this.selectedImageFile = null;
        // Check if already published
        const existente = this.catalogoItems.find(i => i.idPrenda === prenda.idPrenda);
        this.publishForm.reset({ destacado: false });
        if (existente) {
            this.publishForm.patchValue({
                descripcionPublica: existente.descripcionPublica,
                urlFoto: existente.urlFoto ?? prenda.urlFoto ?? '',
                destacado: existente.destacado,
            });
        } else {
            this.publishForm.patchValue({
                urlFoto: prenda.urlFoto ?? '',
            });
        }
        this.imagePreview = this.publishForm.get('urlFoto')?.value || null;
        this.prendasStep = 'publish';
    }

    publishPrenda(): void {
        if (this.publishForm.invalid) { this.publishForm.markAllAsTouched(); return; }

        const currentUrl = this.publishForm.get('urlFoto')?.value?.trim();
        if (!this.selectedImageFile && !currentUrl) {
            this.toast.show('Selecciona una imagen antes de publicar la prenda.', 'error');
            return;
        }

        if (this.selectedImageFile) {
            const formData = new FormData();
            formData.append('imagen', this.selectedImageFile, this.selectedImageFile.name);
            this.http.post<any>(`${this.base}/api/v1/imagenes/prendas`, formData).subscribe({
                next: response => this.publishPrendaWithUrl(response.url),
                error: (error) => this.showImageUploadError(error),
            });
            return;
        }

        this.publishPrendaWithUrl(currentUrl);
    }

    private showImageUploadError(error: HttpErrorResponse): void {
        const backendMessage = error.error?.error || error.error?.mensaje || error.error?.message;
        if (error.status === 0) {
            this.toast.show('No se pudo conectar con el servidor de imagenes.', 'error');
        } else if (error.status === 413) {
            this.toast.show('La imagen supera el limite de 10 MB.', 'error');
        } else if (error.status === 503) {
            this.showSystemAlert('carga de imagenes');
        } else {
            this.toast.show(backendMessage || `No se pudo cargar la imagen (HTTP ${error.status}).`, 'error');
        }
    }

    private publishPrendaWithUrl(urlFoto?: string): void {
        const existente = this.catalogoItems.find(i => i.idPrenda === this.selectedPrenda.idPrenda);

        const data = {
            idPrenda: this.selectedPrenda.idPrenda,
            ...this.publishForm.value,
            urlFoto: urlFoto || this.publishForm.get('urlFoto')?.value,
            activo: true,
        };

        const req = existente
            ? this.http.put(`${this.base}/api/v1/catalogo/items/${existente.idItem ?? existente.id}`, data)
            : this.http.post(`${this.base}/api/v1/catalogo/items`, data);

        req.subscribe({
            next: () => {
                this.toast.show('¡Publicado en el catálogo! ✓');
                this.prendasStep = 'list';
                this.loadPrendas();
            },
            error: () => this.showSystemAlert('publicación en catálogo'),
        });
    }

    despublicarPrenda(prenda: any): void {
        const item = this.catalogoItems.find(i => i.idPrenda === prenda.idPrenda && i.activo !== false);
        if (!item) return;
        if (!confirm(`¿Retirar "${prenda.nombrePrenda}" del catálogo público?`)) return;
        this.http.put(`${this.base}/api/v1/catalogo/items/${item.idItem ?? item.id}`, { ...item, activo: false }).subscribe({
            next: () => {
                item.activo = false;
                this.toast.show('Retirado del catálogo ✓');
                this.loadPrendas();
            },
            error: () => this.showSystemAlert('actualización del catálogo'),
        });
    }

    deletePrenda(id: number): void {
        if (!confirm('¿Eliminar esta prenda del inventario?')) return;
        this.http.delete(`${this.base}/api/v1/prendas/${id}`).subscribe({
            next: () => { this.toast.show('Prenda eliminada ✓'); this.loadPrendas(); },
            error: () => this.showSystemAlert('eliminación de prendas'),
        });
    }

    backToList(): void {
        this.prendasStep = 'list';
        this.selectedPrenda = null;
        this.loadPrendas();
    }

    // ── ÓRDENES ───────────────────────────────────────────────────

    loadOrdenes(): void {
        this.http.get<any[]>(`${this.base}/api/v1/pedidos`).subscribe({
            next: d => this.ordenes = d,
            error: () => { this.ordenes = []; this.showSystemAlert('pedidos y ventas'); },
        });
    }

    openOrdenModal(orden: any): void {
        this.selectedOrden = orden;
        this.showOrdenModal = true;
    }

    closeOrdenModal(): void { this.showOrdenModal = false; }

    updateEstadoOrden(idPedido: number, estado: string): void {
        this.http.patch(`${this.base}/api/v1/pedidos/${idPedido}/estado`, { estadoPedido: estado }).subscribe({
            next: () => { this.toast.show('Estado actualizado ✓'); this.loadOrdenes(); this.closeOrdenModal(); },
            error: () => this.showSystemAlert('actualización de pedidos'),
        });
    }

    getEstadoBadgeClass(estado: string): string {
        const map: Record<string, string> = {
            PENDIENTE: 'badge-warning',
            CONFIRMADO: 'badge-info',
            ENVIADO: 'badge-primary',
            ENTREGADO: 'badge-success',
            CANCELADO: 'badge-danger',
        };
        return map[estado] || 'badge-secondary';
    }

    loadCatalogo(): void {
        this.http.get<any[]>(`${this.base}/api/v1/catalogo/items`).subscribe({
            next: items => this.catalogoItems = items,
            error: () => { this.catalogoItems = []; this.showSystemAlert('catálogo'); },
        });
    }

    loadPagos(): void {
        this.http.get<any[]>(`${this.base}/api/v1/pagos`).subscribe({
            next: data => this.pagos = data,
            error: () => { this.pagos = []; this.showSystemAlert('pagos'); },
        });
    }

    loadClientes(): void {
        this.http.get<any[]>(`${this.base}/api/v1/clientes`).subscribe({
            next: data => this.clientes = data,
            error: () => { this.clientes = []; this.showSystemAlert('clientes'); },
        });
    }

    loadUsuarios(): void {
        this.http.get<any[]>(`${this.base}/api/v1/auth`).subscribe({
            next: data => this.usuarios = data,
            error: () => { this.usuarios = []; this.showSystemAlert('usuarios y roles'); },
        });
    }

    // ── PROMOCIONES ───────────────────────────────────────────────

    loadDescuentos(): void {
        this.loadPrendas();
        this.http.get<any[]>(`${this.base}/api/v1/precios/prendas`).subscribe({
            next: data => this.preciosCatalogo = data,
            error: () => { this.preciosCatalogo = []; },
        });
        this.descuentoSvc.listar().subscribe({
            next: d => this.descuentos = d,
            error: () => { this.descuentos = []; this.showSystemAlert('descuentos del catálogo'); },
        });
    }

    productosPublicados(): { id: number; nombre: string; precio: number; foto: string }[] {
        return (this.catalogoItems || [])
            .filter(item => item.activo !== false)
            .map(item => {
                const prenda = this.prendas.find(p => (p.idPrenda ?? p.id) === item.idPrenda);
                const precio = this.preciosCatalogo.find(p => p.prendaId === item.idPrenda && p.activo !== false);
                return {
                    id: item.idPrenda,
                    nombre: prenda?.nombrePrenda || precio?.nombre || `Producto ${item.idPrenda}`,
                    precio: Number(precio?.precioBase ?? 0),
                    foto: item.urlFoto || '',
                };
            })
            .filter(item => item.precio > 0);
    }

    trackProducto(_index: number, producto: { id: number }): number {
        return producto.id;
    }

    descuentoMarcado(id: number): boolean {
        const ids: number[] = this.descuentoForm.get('prendaIds')?.value || [];
        return ids.includes(id);
    }

    toggleProductoDescuento(id: number): void {
        const control = this.descuentoForm.get('prendaIds');
        const ids: number[] = [...(control?.value || [])];
        const index = ids.indexOf(id);
        if (index >= 0) ids.splice(index, 1);
        else ids.push(id);
        control?.setValue(ids);
        control?.markAsTouched();
    }

    openDescuentoModal(item?: Descuento): void {
        this.editingDescuento = item || null;
        this.descuentoForm.reset({ prendaIds: item?.prendaIds || [] });
        if (item) this.descuentoForm.patchValue({ ...item, prendaIds: item.prendaIds || [] });
        this.showDescuentoModal = true;
    }

    closeDescuentoModal(): void { this.showDescuentoModal = false; }

    saveDescuento(): void {
        const data = this.descuentoForm.getRawValue();
        if (!data.prendaIds?.length) {
            this.toast.show('Elige al menos un producto publicado del catálogo', 'error');
            return;
        }
        if (this.descuentoForm.invalid) {
            this.descuentoForm.markAllAsTouched();
            this.toast.show('Revisa el porcentaje y las fechas. El porcentaje va de 1 a 99.', 'error');
            return;
        }
        const req$ = this.editingDescuento
            ? this.descuentoSvc.actualizar(this.editingDescuento.id, data)
            : this.descuentoSvc.crear(data);
        req$.subscribe({
            next: () => {
                this.toast.show(this.editingDescuento ? 'Descuento actualizado' : 'Descuento creado');
                this.closeDescuentoModal();
                this.loadDescuentos();
            },
            error: () => this.showSystemAlert('guardado de descuentos'),
        });
    }

    deleteDescuento(id: number): void {
        if (!confirm('¿Eliminar este descuento?')) return;
        this.descuentoSvc.eliminar(id).subscribe({
            next: () => { this.toast.show('Descuento eliminado'); this.loadDescuentos(); },
            error: () => this.showSystemAlert('eliminación de descuentos'),
        });
    }

}
