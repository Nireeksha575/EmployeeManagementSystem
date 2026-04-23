// src/main/java/com/example/EmployeeManagementSystem/config/JWTAuth/JWTAuthConfig.java
package com.example.EmployeeManagementSystem.config.JWTauth;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.client.RestTemplate;

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
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
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
                .securityContext(ctx -> ctx
                        .securityContextRepository(new DelegatingSecurityContextRepository(
                                new HttpSessionSecurityContextRepository(),
                                new RequestAttributeSecurityContextRepository()
                        ))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/Authenticate", "/Authenticate/refresh").permitAll()
                        .requestMatchers("/session/login", "/session/logout").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/auth/google/init").permitAll()          // ✅ init endpoint
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/google.html").permitAll()
                        .requestMatchers("/employee-dashboard.html", "/vendor-dashboard.html","/manager-dashboard.html").permitAll()
                        .requestMatchers("/auth/google/init","/auth/google/callback").permitAll()
                        .requestMatchers("/api-keys/generate").authenticated()
                        .anyRequest().authenticated()
                )

//                // ✅ THIS WAS MISSING — the entire oauth2Login block
//                .oauth2Login(oauth -> oauth
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(customOAuth2UserService())
//                        )
//                        .successHandler(oAuth2SuccessHandler())
//                )

                .addFilterBefore(apiKeyFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}