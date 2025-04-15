package com.divergentsl.multitenant.security;

import com.divergentsl.multitenant.dto.LoginUserDto;
import com.divergentsl.multitenant.dto.RegisterUserDto;
import com.divergentsl.multitenant.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.divergentsl.multitenant.enums.Role;
import com.divergentsl.multitenant.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Signup with Role-Based Assignment
    public User signup(RegisterUserDto input) {
        logger.info("Signup attempt for email: {}", input.getEmail());

        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(input.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("Signup failed: Email {} is already in use", input.getEmail());
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create new user
        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(input.getRole() != null ? input.getRole() : Role.USER); // Default role if not provided

        User savedUser = userRepository.save(user);
        logger.info("User {} registered successfully with role {}", savedUser.getEmail(), savedUser.getRole());

        return savedUser;
    }

    // ✅ Authenticate User and Return User Object
    public User authenticate(LoginUserDto input) {
        logger.info("Login attempt for username/email: {}", input.getUsernameOrEmail());

        User user = userRepository.findByEmail(input.getUsernameOrEmail())
                .or(() -> userRepository.findByName(input.getUsernameOrEmail()))
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: User {} not found", input.getUsernameOrEmail());
                    return new UsernameNotFoundException("Invalid credentials");
                });

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), input.getPassword())
            );
            logger.info("User {} authenticated successfully", user.getEmail());
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed: Invalid credentials for {}", input.getUsernameOrEmail());
            throw new BadCredentialsException("Invalid username or password");
        }

        return user;
    }
}
