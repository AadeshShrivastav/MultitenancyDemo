package com.divergentsl.multitenant.web;

import com.divergentsl.multitenant.dto.TenantDTO;
import com.divergentsl.multitenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> createTenant(@RequestBody TenantDTO tenantDTO) {
        if (tenantService.tenantExists(tenantDTO.getTenantId())) {
            return new ResponseEntity<>("Tenant already exists", HttpStatus.BAD_REQUEST);
        }
        
        tenantService.createTenant(tenantDTO.getTenantId());
        return new ResponseEntity<>("Tenant created successfully", HttpStatus.CREATED);
    }
}