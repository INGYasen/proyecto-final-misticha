-- Tabla: Cliente (El comprador internacional o local)
CREATE TABLE IF NOT EXISTS cliente (
                                       id_cliente BIGINT NOT NULL AUTO_INCREMENT,
                                       nombres VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    pais VARCHAR(50),
    ciudad VARCHAR(100),
    direccion_envio VARCHAR(255),
    PRIMARY KEY (id_cliente)
    );

-- Tabla: Pedido (La orden de compra o carrito finalizado)
CREATE TABLE IF NOT EXISTS pedido (
                                      id_pedido BIGINT NOT NULL AUTO_INCREMENT,
                                      id_cliente BIGINT NOT NULL,
                                      fecha_pedido DATETIME NOT NULL,
                                      estado_pedido VARCHAR(50) NOT NULL, -- Ej: "Pendiente de Pago", "Pagado", "Enviado"
    total_pagar DECIMAL(10, 2) NOT NULL,

    PRIMARY KEY (id_pedido),
    -- Esta FK sí se puede porque 'cliente' está en esta misma base de datos
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
    );

-- Tabla: Detalle del Pedido (Las prendas específicas que compró)
CREATE TABLE IF NOT EXISTS detalle_pedido (
                                              id_detalle BIGINT NOT NULL AUTO_INCREMENT,
                                              id_pedido BIGINT NOT NULL,

    -- OJO AQUÍ: id_prenda es una referencia a ms-inventario. NO lleva "CONSTRAINT FOREIGN KEY"
                                              id_prenda BIGINT NOT NULL,

                                              cantidad INT NOT NULL,
                                              precio_unitario DECIMAL(10, 2) NOT NULL,

    PRIMARY KEY (id_detalle),
    -- Esta FK sí se puede porque 'pedido' está en esta misma base de datos
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    );