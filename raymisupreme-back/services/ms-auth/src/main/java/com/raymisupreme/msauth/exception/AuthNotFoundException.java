package com.raymisupreme.msauth.exception;

/**
 * Se lanza cuando no se encuentra un usuario en Keycloak.
 */
public class AuthNotFoundException extends RuntimeException {
    public AuthNotFoundException(String message) {
        super(message);
    }
}
