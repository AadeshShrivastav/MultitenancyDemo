package com.divergentsl.multitenant.filters;

import com.divergentsl.multitenant.context.TenantContext;
import com.divergentsl.multitenant.exception.MissingTenantException;
import com.divergentsl.multitenant.exception.TenantNotFoundException;
import com.divergentsl.multitenant.service.TenantService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TenantFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
    private final TenantService tenantService;
    private final ObjectMapper objectMapper;
    
    public TenantFilter(TenantService tenantService, ObjectMapper objectMapper) {
        this.tenantService = tenantService;
        this.objectMapper = objectMapper;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            String tenantId = request.getHeader("X-Tenant-ID");
            
            // Skip tenant validation for excluded paths
            if (isExcludedPath(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
            
            if (tenantId == null || tenantId.isEmpty()) {
                logger.warn("Request missing X-Tenant-ID header: {}", request.getRequestURI());
                throw new MissingTenantException("X-Tenant-ID header is required");
            }
            
            // Verify tenant exists
            if (!tenantService.tenantExists(tenantId)) {
                logger.error("Invalid tenant ID: {}", tenantId);
                throw new TenantNotFoundException(tenantId);
            }
            
            // Set tenant and proceed
            logger.debug("Setting tenant context to: {}", tenantId);
            TenantContext.setTenant(tenantId);
            
            filterChain.doFilter(request, response);
        } catch (MissingTenantException | TenantNotFoundException e) {
            // Handle tenant-specific exceptions here
            handleTenantException(response, e);
        } catch (Exception e) {
            // Handle other exceptions
            logger.error("Unexpected error in tenant filter", e);
            handleFilterException(response, e);
        } finally {
            // Always clear the tenant context
            TenantContext.clear();
        }
    }
    
    private boolean isExcludedPath(String path) {
        return path.contains("/public/") || 
               path.startsWith("/actuator") || 
               path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs");
    }
    
    private void handleTenantException(HttpServletResponse response, Exception ex) throws IOException {
        int status = (ex instanceof MissingTenantException) ? 
                HttpServletResponse.SC_BAD_REQUEST : 
                HttpServletResponse.SC_NOT_FOUND;
        
        response.setStatus(status);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status);
        errorResponse.put("error", (status == 400) ? "Bad Request" : "Not Found");
        errorResponse.put("message", ex.getMessage());
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
    
    private void handleFilterException(HttpServletResponse response, Exception ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred");
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
