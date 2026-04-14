package com.example.EmployeeManagementSystem.config.JWTauth;

import com.example.EmployeeManagementSystem.Filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class JWTAuthConfig {
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Bean
    @Order(2)
    public SecurityFilterChain jwtAuth(HttpSecurity security) throws Exception{
        security
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(
                        auth->auth
                                .requestMatchers("/employee/register","/employee/register/manager").permitAll()
                                .requestMatchers(HttpMethod.POST,"/vendors").permitAll()
                                .requestMatchers(HttpMethod.POST,"/Authenticate").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }
}
