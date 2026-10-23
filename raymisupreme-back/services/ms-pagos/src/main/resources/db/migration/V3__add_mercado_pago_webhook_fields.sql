ALTER TABLE pago
    ADD COLUMN mercado_pago_payment_id VARCHAR(50) NULL,
    ADD COLUMN status_detail VARCHAR(100) NULL,
    ADD COLUMN fecha_confirmacion DATETIME NULL;

CREATE UNIQUE INDEX uk_pago_mercado_pago_payment_id ON pago (mercado_pago_payment_id);
