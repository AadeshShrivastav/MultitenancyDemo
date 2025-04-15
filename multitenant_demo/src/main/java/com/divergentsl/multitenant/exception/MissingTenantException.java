package com.divergentsl.multitenant.exception;

public class MissingTenantException extends RuntimeException {
    
    public MissingTenantException(String message) {
        super(message);
    }
} 