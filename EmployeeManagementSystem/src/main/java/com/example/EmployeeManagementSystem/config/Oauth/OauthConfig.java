//package com.example.EmployeeManagementSystem.config.Oauth;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class OauthConfig {
//    @Bean
//    @Order(2)
//    public SecurityFilterChain OauthFilterChain(HttpSecurity http){
//        http
//                .securityMatcher("/google/**","/oauth2/**", "/login/oauth2/**")
//                .authorizeHttpRequests(
//                        auth->auth.
//                                anyRequest().authenticated()
//                )
//                .oauth2Login(oauth -> oauth
//                        .defaultSuccessUrl("/google/login", true));
//        return http.build();
//    }
//
//}
