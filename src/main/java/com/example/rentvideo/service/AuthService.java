package com.example.rentvideo.service;

import com.example.rentvideo.model.User;
import com.example.rentvideo.repository.UserRepository;
import com.example.rentvideo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String email, String password) throws Exception {
        try {
            // Authenticate email and password
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            throw new Exception("Invalid email or password");
        }

        // Load user from database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

                // Convert your User entity to UserDetails
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        Collections.emptyList()
);
return jwtUtil.generateToken(userDetails);
    }

    public String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
}
}
