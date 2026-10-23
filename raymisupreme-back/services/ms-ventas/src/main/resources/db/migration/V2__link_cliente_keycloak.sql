ALTER TABLE cliente
    ADD COLUMN apellidos VARCHAR(150) NULL,
    ADD COLUMN username VARCHAR(80) NULL,
    ADD COLUMN keycloak_user_id VARCHAR(80) NULL;

UPDATE cliente
SET apellidos = COALESCE(apellidos, ''),
    username = COALESCE(username, CONCAT('cliente_', id_cliente)),
    keycloak_user_id = COALESCE(keycloak_user_id, CONCAT('legacy_', id_cliente));

ALTER TABLE cliente
    MODIFY apellidos VARCHAR(150) NOT NULL,
    MODIFY username VARCHAR(80) NOT NULL,
    MODIFY keycloak_user_id VARCHAR(80) NOT NULL,
    ADD CONSTRAINT uk_cliente_username UNIQUE (username),
    ADD CONSTRAINT uk_cliente_keycloak_user UNIQUE (keycloak_user_id);
