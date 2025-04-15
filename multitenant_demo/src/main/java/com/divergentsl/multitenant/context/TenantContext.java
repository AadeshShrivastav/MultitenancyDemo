package com.divergentsl.multitenant.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantContext {
    private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenant(String tenant) {
        logger.info("Setting tenant context to: " + tenant);
        currentTenant.set(tenant);
    }

    public static String getTenant() {
        logger.info("Getting tenant context: " + currentTenant.get());
        return currentTenant.get();
    }

    public static void clear() {
        logger.info("Clearing tenant context");
        currentTenant.remove();
    }
}
