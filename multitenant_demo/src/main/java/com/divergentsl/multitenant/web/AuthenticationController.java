package com.divergentsl.multitenant.web;

import com.divergentsl.multitenant.dto.LoginUserDto;
import com.divergentsl.multitenant.dto.RegisterUserDto;
import com.divergentsl.multitenant.entity.User;
import com.divergentsl.multitenant.security.AuthenticationService;
import com.divergentsl.multitenant.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divergentsl.multitenant.dto.LoginResponse;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        logger.info("Signup request received for email: {}", registerUserDto.getEmail());
        User newUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setMessage("Login successful");

            return ResponseEntity.ok(loginResponse);
        } catch (UsernameNotFoundException e) {
            logger.error("Authentication failed: {}", e.getMessage(), e);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
