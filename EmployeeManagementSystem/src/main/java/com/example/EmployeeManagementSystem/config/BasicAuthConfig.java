package com.example.EmployeeManagementSystem.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity
public class BasicAuthConfig {
    @Bean
   public SecurityFilterChain basicAuth(HttpSecurity security){
      security
               .csrf(csrf-> csrf.disable())
               .authorizeHttpRequests(
                       auth->auth
                               .requestMatchers("/employee/register","/employee/register/manager").permitAll()
                               .anyRequest().authenticated()
               )
               .httpBasic(Customizer.withDefaults());
       return security.build();
   }

   @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
   }

   @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder){
       DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider(userDetailsService);
       authenticationProvider.setPasswordEncoder(passwordEncoder);
       return new ProviderManager(authenticationProvider);
   }

}
