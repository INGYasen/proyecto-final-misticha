ALTER TABLE pago
    ADD COLUMN preference_id VARCHAR(100) NULL,
    ADD COLUMN moneda VARCHAR(3) NULL,
    ADD COLUMN proveedor_pago VARCHAR(30) NULL;

CREATE UNIQUE INDEX uk_pago_preference_id ON pago (preference_id);
