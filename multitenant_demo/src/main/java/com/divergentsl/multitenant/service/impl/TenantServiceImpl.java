package com.divergentsl.multitenant.service.impl;

import com.divergentsl.multitenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class TenantServiceImpl implements TenantService {
    private static final Logger logger = Logger.getLogger(TenantServiceImpl.class.getName());

    private final JdbcTemplate jdbcTemplate;
    private final Set<String> existingTenants = new HashSet<>();
    private final DataSource dataSource;
    
    @Autowired
    public TenantServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        // Initialize with default schema
        existingTenants.add("public");
        loadExistingTenants();
    }
    
    private void loadExistingTenants() {
        jdbcTemplate.query(
            "SELECT schema_name FROM information_schema.schemata",
            (rs, rowNum) -> rs.getString("schema_name")
        ).forEach(schema -> {
            if (!schema.startsWith("pg_") && !schema.equals("information_schema")) {
                existingTenants.add(schema);
            }
        });
    }
    
    public boolean tenantExists(String tenantId) {
        return existingTenants.contains(tenantId);
    }
    
    public void createTenant(String tenantId) {
        if (!tenantExists(tenantId)) {
            try (Connection connection = dataSource.getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    // Create schema
                    statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenantId);
                    
                    // Set search path to new schema
                    statement.execute("SET search_path TO " + tenantId);
                    
                    // Create tables in the new schema
                    createAppUserTable(statement, tenantId);
                    createThreadTable(statement, tenantId);
                    createMessageTable(statement, tenantId);
                    
                    existingTenants.add(tenantId);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error creating tenant schema: " + tenantId, e);
            }
        }
    }

    private void createAppUserTable(Statement statement, String tenantId) throws SQLException {
        boolean tableExists = false;
        try (ResultSet rs = statement.executeQuery(
                "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = '" + 
                tenantId + "' AND table_name = 'app_user')")) {
            if (rs.next()) {
                tableExists = rs.getBoolean(1);
            }
        }
        
        if (!tableExists) {
            statement.execute(
                "CREATE TABLE " + tenantId + ".app_user (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(50) " +
                ")"
            );
            logger.info("Created app_user table in schema: " + tenantId);
        }
    }
    
    private void createThreadTable(Statement statement, String tenantId) throws SQLException {
        boolean tableExists = false;
        try (ResultSet rs = statement.executeQuery(
                "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = '" + 
                tenantId + "' AND table_name = 'thread')")) {
            if (rs.next()) {
                tableExists = rs.getBoolean(1);
            }
        }
        
        if (!tableExists) {
            statement.execute(
                "CREATE TABLE " + tenantId + ".thread (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) " +
                ")"
            );
            logger.info("Created thread table in schema: " + tenantId);
        }
    }
    
    private void createMessageTable(Statement statement, String tenantId) throws SQLException {
        boolean tableExists = false;
        try (ResultSet rs = statement.executeQuery(
                "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = '" + 
                tenantId + "' AND table_name = 'message')")) {
            if (rs.next()) {
                tableExists = rs.getBoolean(1);
            }
        }
        
        if (!tableExists) {
            statement.execute(
                "CREATE TABLE " + tenantId + ".message (" +
                "id SERIAL PRIMARY KEY, " +
                "thread_id BIGINT, " +
                "user_id BIGINT, " +
                "content TEXT " +
                ")"
            );
            logger.info("Created message table in schema: " + tenantId);
        }
    }
    
    public void deleteTenant(String tenantId) {
        if (tenantExists(tenantId) && !tenantId.equals("public")) {
            jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + tenantId + " CASCADE");
            existingTenants.remove(tenantId);
        }
    }
} 