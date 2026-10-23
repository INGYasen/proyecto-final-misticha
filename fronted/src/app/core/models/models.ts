// ================================================================
// MODELOS — RaymiSupreme
// ================================================================

export interface Prenda {
  id: number;
  idLote?: number;
  idCategoria: number;
  publico?: 'MUJER' | 'VARON' | 'NINOS' | 'UNISEX';
  codigoSku: string;
  talla: string;
  color: string;
  material: string;
  stockDisponible: number;
  // Desde catálogo:
  nombre?: string;
  descripcionPublica?: string;
  imagen?: string;
  precio?: number;
  precioBase?: number;
  destacado?: boolean;
  activo?: boolean;
}

export interface CatalogoItem {
  id: number;
  idPrenda: number;
  urlFoto: string;
  descripcionPublica: string;
  destacado: boolean;
  activo: boolean;
  prenda?: Prenda;
  precio?: PrecioPrenda;
}

export interface Categoria {
  id: number;
  nombre: string;
  descripcion: string;
}

export interface PrecioPrenda {
  id: number;
  prendaId: number;
  nombre: string;
  precioBase: number;
  descuentoId?: number;
  descuento?: Descuento;
  precioFinal?: number;
}

export interface Descuento {
  id: number;
  codigo: string;
  descripcion: string;
  porcentaje: number;
  fechaInicio: string;
  fechaFin: string;
}

export interface Promocion {
  id: number;
  titulo: string;
  descripcion: string;
  imagen?: string;
}

// ── Ventas ───────────────────────────────────────────────────────

export interface Cliente {
  idCliente?: number;
  nombres: string;
  apellidos?: string;
  username?: string;
  email: string;
  pais: string;
  ciudad: string;
  direccionEnvio: string;
  keycloakUserId?: string;
}

export interface Pedido {
  idPedido?: number;
  idCliente: number;
  fechaPedido: string;
  estadoPedido: EstadoPedido;
  totalPagar: number;
  detalles?: DetallePedido[];
}

export type EstadoPedido = 'PENDIENTE' | 'CONFIRMADO' | 'ENVIADO' | 'ENTREGADO' | 'CANCELADO';

export interface DetallePedido {
  idDetalle?: number;
  idPedido: number;
  idPrenda: number;
  cantidad: number;
  precioUnitario: number;
  prenda?: Prenda;
}

export type MetodoPago = 'MERCADO_PAGO';

export interface MercadoPagoPreferenciaResponse {
  preferenceId: string;
  initPoint: string;
}

export interface MercadoPagoPaymentRequest {
  pedidoId: number;
  token: string;
  transaction_amount: number;
  payment_method_id: string;
  issuer_id?: string;
  device_id?: string;
  installments: number;
  payer: {
    email: string;
    identification?: { type?: string; number?: string };
  };
}

export interface MercadoPagoPaymentResponse {
  paymentId: string;
  status: 'approved' | 'rejected' | 'pending' | 'in_process' | 'error';
  status_detail: string;
  message: string;
  error?: string;
}

export interface StripeCheckoutResponse { sessionId: string; url: string; }
export interface StripeSessionResponse {
  sessionId: string; paymentStatus: string; status: string;
  amountTotal: number; currency: string; paymentIntent?: string; ventaId: string;
}

export interface PagoRequest {
  pedidoId: number;
  clienteId: number;
  monto: number;
  metodoPago: MetodoPago;
}

// ── Carrito local ─────────────────────────────────────────────────

export interface CartItem {
  prendaId: number;
  nombrePrenda: string;
  precio: number;
  cantidad: number;
  urlFoto?: string;
  talla?: string;
  color?: string;
  serverItemId?: number;
}

// ── Auth ──────────────────────────────────────────────────────────

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  access_token: string;
  refresh_token?: string;
  id_token?: string;
  token_type: string;
  expires_in: number;
}

export interface UserInfo {
  sub: string;           // username / keycloak sub
  email: string;
  given_name?: string;
    family_name?: string;
    preferred_username?: string;
  realm_access?: { roles: string[] };
}

// ── Home ──────────────────────────────────────────────────────────

export interface HomeData {
  productos: CatalogoItem[];
  categorias: Categoria[];
  promociones: Promocion[];
}
