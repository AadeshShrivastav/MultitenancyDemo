package com.divergentsl.multitenant.multitenancy;

import com.divergentsl.multitenant.context.TenantContext;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TenantSchemaResolver implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT = "public";
    private static final Logger logger = LoggerFactory.getLogger(TenantSchemaResolver.class);


    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getTenant();
        logger.info("Resolving tenant identifier: " + tenantId);
        return (tenantId != null) ? tenantId : DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        logger.info("Validating existing current sessions");
        return true;
    }
} 