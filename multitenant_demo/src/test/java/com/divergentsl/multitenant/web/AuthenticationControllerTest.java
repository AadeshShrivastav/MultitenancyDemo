package com.divergentsl.multitenant.web;

import com.divergentsl.multitenant.TestApplication;
import com.divergentsl.multitenant.config.TestSecurityConfig;
import com.divergentsl.multitenant.dto.LoginUserDto;
import com.divergentsl.multitenant.dto.RegisterUserDto;
import com.divergentsl.multitenant.entity.User;
import com.divergentsl.multitenant.enums.Role;
import com.divergentsl.multitenant.security.AuthenticationService;
import com.divergentsl.multitenant.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = TestApplication.class)
@Import(TestSecurityConfig.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterUserDto registerUserDto;
    private LoginUserDto loginUserDto;
    private User user;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Test user setup
        registerUserDto = new RegisterUserDto();
        registerUserDto.setName("Test User");
        registerUserDto.setEmail("test@example.com");
        registerUserDto.setPassword("password123");
        registerUserDto.setRole(Role.USER);

        loginUserDto = new LoginUserDto();
        loginUserDto.setUsernameOrEmail("test@example.com");
        loginUserDto.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        jwtToken = "test.jwt.token";
    }

    @Test
    @WithMockUser
    void testRegisterUser() throws Exception {
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/signup")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value(user.getRole().toString()));
    }

    @Test
    @WithMockUser
    void testLoginUser_Success() throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        mockMvc.perform(post("/api/v1/auth/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken));
    }

    @Test
    @WithMockUser
    void testLoginUser_InvalidCredentials() throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isUnauthorized());
    }
}