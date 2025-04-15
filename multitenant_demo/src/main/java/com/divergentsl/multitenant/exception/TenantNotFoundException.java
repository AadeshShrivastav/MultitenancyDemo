package com.divergentsl.multitenant.exception;

public class TenantNotFoundException extends RuntimeException {
    
    public TenantNotFoundException(String tenantId) {
        super("Tenant not found: " + tenantId);
    }
} 