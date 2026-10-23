CREATE TABLE pago (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pedido_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    referencia_pago VARCHAR(100),
    fecha_pago DATETIME NOT NULL,
    creado_en DATETIME NOT NULL,
    actualizado_en DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE comprobante (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pago_id BIGINT NOT NULL,
    numero_comprobante VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha_emision DATETIME NOT NULL,
    monto_total DECIMAL(10, 2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_comprobante_numero UNIQUE (numero_comprobante),
    CONSTRAINT fk_comprobante_pago FOREIGN KEY (pago_id) REFERENCES pago (id)
);
