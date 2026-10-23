package com.raymisupreme.msventas.event;

import com.raymisupreme.msventas.dto.PedidoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventoPublisher {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventoPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PedidoEventoPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void pedidoCreado(PedidoResponse pedido) {
        String mensaje = "{\"idPedido\":" + pedido.getIdPedido()
                + ",\"idCliente\":" + pedido.getIdCliente()
                + ",\"estado\":\"" + pedido.getEstadoPedido() + "\"}";
        try {
            kafkaTemplate.send("pedidos", String.valueOf(pedido.getIdPedido()), mensaje)
                    .whenComplete((resultado, error) -> {
                        if (error != null) {
                            log.warn("Kafka no recibio el pedido {}: {}", pedido.getIdPedido(), error.getMessage());
                        }
                    });
        } catch (Exception ex) {
            log.warn("Kafka no esta disponible. El pedido {} sigue guardado: {}", pedido.getIdPedido(), ex.getMessage());
        }
    }
}
