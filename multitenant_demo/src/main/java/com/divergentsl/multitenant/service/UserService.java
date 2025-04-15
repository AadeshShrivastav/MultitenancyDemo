package com.divergentsl.multitenant.service;

import java.util.List;
import java.util.Optional;

import com.divergentsl.multitenant.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.divergentsl.multitenant.enums.Role;
import com.divergentsl.multitenant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import java.sql.SQLException;
import com.divergentsl.multitenant.context.TenantContext;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final DataSource dataSource;

    public UserService(UserRepository userRepository, DataSource dataSource) {
        this.userRepository = userRepository;
        this.dataSource = dataSource;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        String tenantId = TenantContext.getTenant();
        logger.info("Loading user by username: {} for tenant: {}", username, tenantId);
        
        try (Connection connection = dataSource.getConnection()) {
            // Set schema for this specific connection
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET search_path TO " + tenantId);
                
                // Use JdbcTemplate with this specific connection
                JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, false));
                
                // Query using the tenant-specific connection
                List<User> users = jdbcTemplate.query(
                    "SELECT * FROM app_user WHERE email = ?",
                    (rs, rowNum) -> {
                        User user = new User();
                        user.setId(rs.getLong("id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(rs.getString("password"));
                        user.setRole(Role.valueOf(rs.getString("role")));
                        return user;
                    },
                    username
                );
                
                if (!users.isEmpty()) {
                    return users.get(0);
                }
            }
        } catch (SQLException e) {
            logger.error("Error accessing database for tenant: {}", tenantId, e);
        }
        
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
