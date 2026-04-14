package com.example.EmployeeManagementSystem.Service;

import com.example.EmployeeManagementSystem.Exception.VendorNotFoundException;
import com.example.EmployeeManagementSystem.Repository.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("vendorUserDetailsService")
public class VendorUserDetailsService implements UserDetailsService {
     @Autowired
    VendorRepo vendorRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return vendorRepo.findByEmail(username).orElseThrow(
                ()->new VendorNotFoundException("vendor Not found")
        );
    }
}
