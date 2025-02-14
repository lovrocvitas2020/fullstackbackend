package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.ResetPasswordRequest;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.example.fullstackcrudreact.fullstackbackend.security.CustomUserDetailsService;
import com.example.fullstackcrudreact.fullstackbackend.service.EmailService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AuthController(CustomUserDetailsService customUserDetailsService, EmailService emailService) {
        this.customUserDetailsService = customUserDetailsService;
        this.emailService = emailService;
    }

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<String> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {


        System.out.println("resetPassword START - test");

        try {
            // Validacija emaila
            if (!isValidEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Invalid email address.");
            }

            // Provjera da li korisnik postoji
            // Check if User Exists   
         User user = userRepository.findById(id)
         .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
       
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            // Generiraj token za reset lozinke
            String token = generateResetToken(request.getEmail());

            System.out.println("resetPassword request.getEmail():"+request.getEmail());

            // Pošalji email s tokenom
            emailService.sendResetPasswordEmail(request.getEmail(), token);

            return ResponseEntity.ok("Reset password email sent successfully!");
        } catch (Exception e) {
            logger.error("Failed to send reset password email:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send reset password email.");
        }
    }

    private String generateResetToken(String email) {
        // Generiraj sigurni token
        String token = UUID.randomUUID().toString();

        // Pohrani token u bazi podataka (ovdje se simulira)
        logger.info("Generated reset token for email: {}", email);
        return token;
    }

    private boolean isValidEmail(String email) {
        // Validacija emaila
        return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    @GetMapping("/authme")
    public ResponseEntity<User> getLoggedInUser() {
        // Get the username of the logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication is available (to avoid potential nulls if not authenticated)
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Retrieve user details using custom service
        String username = auth.getName();  // Get the logged-in username
        User user = (User) customUserDetailsService.loadUserByUsername(username);

        // If user is not found, handle accordingly
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(user);
    }
}
