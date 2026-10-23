package com.raymisupreme.msinventario.controller;

import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventario")
public class GatewayInstanciasController {

    private final Environment environment;

    public GatewayInstanciasController(Environment environment) {
        this.environment = environment;
    }

    // Roles: solo SUPER_ADMIN consulta informacion interna de instancia.
    @GetMapping("/instancia")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Map<String, String> instancia() {
        return Map.of(
                "app", environment.getProperty("spring.application.name", "ms-inventario"),
                "port", environment.getProperty("local.server.port", "N/A"),
                "host", obtenerHost()
        );
    }

    private String obtenerHost() {
        String hostname = environment.getProperty("HOSTNAME");
        if (hostname != null && !hostname.isBlank()) return hostname;
        String computerName = environment.getProperty("COMPUTERNAME");
        if (computerName != null && !computerName.isBlank()) return computerName;
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "desconocido";
        }
    }
}
