package com.example.EmployeeManagementSystem.Controller;

import com.example.EmployeeManagementSystem.DTO.AuthRequest;
import com.example.EmployeeManagementSystem.Util.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Authenticate")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public String authenticateToken(@RequestBody AuthRequest authRequest){
        try{
            System.out.println("1. Trying to authenticate user: " + authRequest.getUsername());
            System.out.println("2. Password length: " + authRequest.getPassword().length());

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            System.out.println("3. Authentication successful! User: " + auth.getName());
            System.out.println("4. Authorities: " + auth.getAuthorities());

            String token = jwtUtil.generateToken(authRequest.getUsername());
            System.out.println("5. Token generated: " + token);

            return token;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();  // This will show full stack trace
            throw new RuntimeException(e);
        }
    }
}
