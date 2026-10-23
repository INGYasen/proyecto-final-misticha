ALTER TABLE pago
    ADD COLUMN stripe_session_id VARCHAR(255) NULL,
    ADD COLUMN stripe_payment_intent_id VARCHAR(255) NULL;

CREATE UNIQUE INDEX uk_pago_stripe_session_id ON pago (stripe_session_id);
