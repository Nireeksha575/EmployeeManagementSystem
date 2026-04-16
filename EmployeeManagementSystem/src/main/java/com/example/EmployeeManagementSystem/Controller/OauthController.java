package com.example.EmployeeManagementSystem.Controller;

import com.example.EmployeeManagementSystem.Entity.Employee;
import com.example.EmployeeManagementSystem.Enum.Role;
import com.example.EmployeeManagementSystem.Repository.EmployeeRepo;
import com.example.EmployeeManagementSystem.Service.CombinedUserDetailService;
import com.example.EmployeeManagementSystem.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
public class OauthController {
    private final EmployeeRepo employeeRepo;
    public OauthController(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String client_id;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String client_secret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    CombinedUserDetailService combinedUserDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/callback")
    public ResponseEntity<?> handelGoogleCallback(@RequestParam String code){
        try {
            System.out.println("CODE RECEIVED: " + code);
            String tokenEndpoint="https://oauth2.googleapis.com/token";
            MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
            params.add("code",code);
            params.add("client_id",client_id);
            params.add("client_secret",client_secret);
            params.add("redirect_uri","http://localhost:8080/oauth.html");
            params.add("grant_type","authorization_code");

            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,String>> request=new HttpEntity<>(params,headers);
            ResponseEntity<Map> tokenResponse=restTemplate.postForEntity(tokenEndpoint,request,Map.class);
            String idToken=(String) tokenResponse.getBody().get("id_token");
            String UserInfoUrl="https://oauth2.googleapis.com/tokeninfo?id_token="+idToken;
            ResponseEntity<Map> userInfoResponse=restTemplate.getForEntity(UserInfoUrl, Map.class);

            if(userInfoResponse.getStatusCode()== HttpStatus.OK){
                Map<String,Object> userInfo=userInfoResponse.getBody();
                String email=(String) userInfo.get("email");
                UserDetails userDetails=null;

                try{
                    userDetails=combinedUserDetailService.loadUserByUsername(email);
                } catch (UsernameNotFoundException e) {
                    Employee employee=new Employee();
                    employee.setEmail(email);
                    employee.setName(email);
                    employee.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    employee.setRole(Role.EMPLOYEE);
                    employeeRepo.save(employee);
                    userDetails=combinedUserDetailService.loadUserByUsername(email);
                }
                String token= jwtUtil.generateToken(email);
                return ResponseEntity.ok(Collections.singletonMap("token",token));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }



}
