// src/main/java/com/example/EmployeeManagementSystem/config/JWTAuth/JWTAuthConfig.java
package com.example.EmployeeManagementSystem.config.JWTAuth;

import com.example.EmployeeManagementSystem.Filter.ApiKeyFilter;
import com.example.EmployeeManagementSystem.Filter.JwtAuthFilter;
import com.example.EmployeeManagementSystem.security.ApiKeyAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class JWTAuthConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ApiKeyAuthenticationProvider apiKeyAuthProvider;

    public JWTAuthConfig(JwtAuthFilter jwtAuthFilter,
                         ApiKeyAuthenticationProvider apiKeyAuthProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.apiKeyAuthProvider = apiKeyAuthProvider;
    }

    @Bean
    public ApiKeyFilter apiKeyFilter() {
        return new ApiKeyFilter();
    }

    // DON'T create a new authenticationManager bean - use existing or rename
    // Instead, just add the provider to existing auth manager
    // Remove or comment out this bean if BasicAuthConfig already provides it

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/Authenticate", "/session/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api-keys/generate").authenticated()  // Requires auth
                        .anyRequest().authenticated()
                )
                // Add API Key filter BEFORE JWT filter
                .addFilterBefore(apiKeyFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}