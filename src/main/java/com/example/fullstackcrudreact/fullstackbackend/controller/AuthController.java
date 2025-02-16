package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.PasswordResetToken;
import com.example.fullstackcrudreact.fullstackbackend.model.ResetPasswordRequest;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.PasswordResetTokenRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.example.fullstackcrudreact.fullstackbackend.security.CustomUserDetailsService;
import com.example.fullstackcrudreact.fullstackbackend.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public AuthController(CustomUserDetailsService customUserDetailsService, EmailService emailService) {
        this.customUserDetailsService = customUserDetailsService;
        this.emailService = emailService;
    }

    @PostMapping("/send-reset-request/{id}")
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

            // Save token to password_reset_token
            PasswordResetToken resetToken = new PasswordResetToken(token, request.getEmail(), Timestamp.from(Instant.now().plusSeconds(3600)));
            passwordResetTokenRepository.save(resetToken);


            return ResponseEntity.ok("Reset password email sent successfully!");
        } catch (Exception e) {
            logger.error("Failed to send reset password email:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send reset password email.");
        }
    }


    @PostMapping("/confirm-reset-password/{id}")
    public ResponseEntity<String> confirmResetPassword(@PathVariable Long id, @RequestBody String requestData) {
        try {
            // Parse the JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            ResetPasswordRequest request = objectMapper.readValue(requestData, ResetPasswordRequest.class);
    
            // Log the parsed data
            logger.info("Confirming password reset for user ID: {}", id);
            logger.info("Request payload: userId={}, token={}, newPassword={}",
                        request.getUserId(), request.getToken(), request.getNewPassword());
    
            // The rest of your code remains the same
    
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    
            Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(request.getToken());

            logger.info("optionalToken.isPresent(): "+optionalToken.isPresent());

            if (optionalToken.isPresent()) {
                PasswordResetToken passwordResetToken = optionalToken.get();
    
                logger.info("passwordResetToken.getEmail(): {}", passwordResetToken.getEmail());
                logger.info("passwordResetToken.getExpiryDate(): {}", passwordResetToken.getExpiryDate());
                logger.info("request.getNewPassword(): {}", request.getNewPassword());
    
                // Check if the token is valid
                if (passwordResetToken.getExpiryDate().after(Timestamp.from(Instant.now()))) {
                    // Update the user's password
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    user.setUpdatedOn(Timestamp.from(Instant.now()));
                    userRepository.save(user);
    
                    // Optionally, delete the token after use
                   // passwordResetTokenRepository.deleteByToken(request.getToken());
    
                    return ResponseEntity.ok("Password reset successfully!");
                } else {
                    logger.warn("Token is invalid or expired for user ID: {}", id);
                    return ResponseEntity.badRequest().body("Invalid or expired token.");
                }
            } else {
                logger.warn("Token not found for user ID: {}", id);
                return ResponseEntity.badRequest().body("Invalid or expired token.");
            }
        } catch (Exception e) {
            logger.error("Failed to reset password for user ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password.");
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

    private boolean isValidToken(String token, String email) {

        System.out.println("isValidToken token: "+token+" email: "+email);

        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);


        if (optionalToken.isPresent()) {
            PasswordResetToken passwordResetToken = optionalToken.get();
            logger.info("Checking token: {}, associated email: {}", token, passwordResetToken.getEmail());
            if (passwordResetToken.getEmail().equals(email) && passwordResetToken.getExpiryDate().after(Timestamp.from(Instant.now()))) {
                return true;
            } else {
                logger.warn("Token is expired or email does not match.");
            }
        } else {
            logger.warn("Token not found.");
        }
        return false;
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
