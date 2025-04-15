package com.divergentsl.multitenant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.divergentsl.multitenant.service.UserService;

@Configuration
public class ApplicationConfiguration {
    // private static final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);
    // private final UserRepository userRepository;

    // public ApplicationConfiguration(UserRepository userRepository) {
    //     this.userRepository = userRepository;
    // }

    // @Bean
    // UserDetailsService userDetailsService() {
    //     return username -> userRepository.findByEmail(username)
    //             .or(() -> userRepository.findByName(username))
    //             .orElseThrow(() -> {
    //                 logger.error("User not found with username or email: {}", username);
    //                 return new UsernameNotFoundException("User not found");
    //             });
    // }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
