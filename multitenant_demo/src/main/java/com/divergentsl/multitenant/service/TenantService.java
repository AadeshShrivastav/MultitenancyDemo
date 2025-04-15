package com.divergentsl.multitenant.service;


/**
 * Service interface for tenant management operations
 */
public interface TenantService {

    /**
     * Check if a tenant exists by tenant ID
     *
     * @param tenantId the unique tenant identifier
     * @return true if tenant exists, false otherwise
     */
    boolean tenantExists(String tenantId);

    /**
     * Create a new tenant with the specified ID
     *
     * @param tenantId the tenant identifier to create
     */
    void createTenant(String tenantId);

}
