package com.raymisupreme.msauth.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura el Keycloak Admin Client que usa Service Account
 * (client_credentials) para autenticarse contra la Admin API de Keycloak.
 *
 * El cliente "ms-usuarios-admin" en el realm raymisupreme tiene
 * "Service Account Enabled" y los roles manage-users, query-users, view-users
 * asignados en client roles de "realm-management".
 */
@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    /**
     * Bean reutilizable del Admin Client.
     * Keycloak gestiona internamente el token y lo renueva cuando expira.
     *
     * FIX: Se usa `realm` (variable = "raymisupreme") en lugar de "master" hardcodeado.
     * El cliente ms-usuarios-admin existe en el realm raymisupreme, no en master.
     * Autenticarse contra master con un cliente que no existe ahí devuelve 401
     * en cada llamada a la Admin API.
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)              // FIX: era "master" hardcodeado — debe ser el realm del cliente
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    @Bean
    public String keycloakRealm() {
        return realm;
    }
}