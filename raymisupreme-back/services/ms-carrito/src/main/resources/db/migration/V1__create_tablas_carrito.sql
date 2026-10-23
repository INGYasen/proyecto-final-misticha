CREATE TABLE carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    creado_en DATETIME NOT NULL,
    actualizado_en DATETIME
);

CREATE TABLE item_carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    prenda_id BIGINT NOT NULL,
    nombre_prenda VARCHAR(200) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    agregado_en DATETIME NOT NULL,
    CONSTRAINT fk_item_carrito
        FOREIGN KEY (carrito_id) REFERENCES carrito(id)
);
