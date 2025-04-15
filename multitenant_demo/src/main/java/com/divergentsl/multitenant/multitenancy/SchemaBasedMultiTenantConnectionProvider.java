package com.divergentsl.multitenant.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Serial;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaBasedMultiTenantConnectionProvider
        implements MultiTenantConnectionProvider {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(SchemaBasedMultiTenantConnectionProvider.class);
    private static final String DEFAULT_TENANT = "public";
    private final DataSource dataSource;

    public SchemaBasedMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        logger.info("Getting any connection");
        Connection connection = dataSource.getConnection();
        // Reset to default schema
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET search_path TO " + DEFAULT_TENANT);
        } catch (SQLException e) {
            logger.error("Could not set search_path to default schema", e);
            connection.close();
            throw e;
        }
        logger.info("Returning connection");
        return connection;
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(Object o) throws SQLException {
        return getConnection(o.toString());
    }

    @Override
    public void releaseConnection(Object o, Connection connection) throws SQLException {
        releaseConnection(o.toString(), connection);
    }

    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            if (tenantIdentifier != null) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(String.format("SET search_path TO %s", tenantIdentifier));
                    // Verify the schema was set
                    try (ResultSet rs = stmt.executeQuery("SHOW search_path")) {
                        if (rs.next()) {
                            logger.info("Current search path: " + rs.getString(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", e);
            throw e;
        }
        return connection;
    }

    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        logger.info("Releasing connection for tenant: " + tenantIdentifier);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET search_path TO " + DEFAULT_TENANT);
        } catch (SQLException e) {
            logger.warn("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", e);
        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

}