package com.example.EmployeeManagementSystem.config.JWTauth;

import com.example.EmployeeManagementSystem.Filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class JWTAuthConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    @Order(3)
    public SecurityFilterChain jwtAuth(HttpSecurity security) throws Exception {
        security
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permit Swagger UI and API documentation
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/leavemanagement.html",
                                "/oauth2/authorization/google"
                        ).permitAll()
                        // Permit authentication endpoints
                        .requestMatchers("/auth/login", "/Authenticate").permitAll()
                        .requestMatchers("/session/login", "/session/logout", "/session/me").permitAll()
                        // Permit registration endpoints
                        .requestMatchers("/employee/register", "/employee/register/manager").permitAll()
                        .requestMatchers(HttpMethod.POST, "/vendors").permitAll()
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return security.build();
    }
}