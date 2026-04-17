package com.example.EmployeeManagementSystem.Controller;

import com.example.EmployeeManagementSystem.Entity.Employee;
import com.example.EmployeeManagementSystem.Repository.EmployeeRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class OauthController {
    private final EmployeeRepo employeeRepo;

    public OauthController(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @GetMapping("/google/login")
    public ResponseEntity<?> user(OAuth2AuthenticationToken auth){

        String email = auth.getPrincipal().getAttribute("email");

        Optional<Employee> employee=employeeRepo.findByEmail(email);
        if(employee.isPresent()){
            return ResponseEntity.ok(Map.of(
                    "Logged in user: ",email,
                    "Employee name:",employee.get().getName(),
                    "Employee dept:",employee.get().getDept(),
                    "Employee authorities:",employee.get().getAuthorities()
            ));
        }

        return ResponseEntity.ok("User: " + email+" doesn't have any account");
    }
}
