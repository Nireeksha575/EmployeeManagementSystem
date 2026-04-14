package com.example.EmployeeManagementSystem.Controller;

import com.example.EmployeeManagementSystem.DTO.AuthRequest;
import com.example.EmployeeManagementSystem.Util.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
          System.out.println(authRequest.getUsername());
          authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(
                          authRequest.getUsername(),
                          authRequest.getPassword()
                  )
          );
          return jwtUtil.generateToken(authRequest.getUsername());
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }
}
