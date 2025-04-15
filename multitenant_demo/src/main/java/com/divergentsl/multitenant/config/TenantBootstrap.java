package com.divergentsl.multitenant.config;

import com.divergentsl.multitenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TenantBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    private final TenantService tenantService;

    @Autowired
    public TenantBootstrap(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Create default tenants if they don't exist
        tenantService.createTenant("tenant1");
        tenantService.createTenant("tenant2");
    }
} 