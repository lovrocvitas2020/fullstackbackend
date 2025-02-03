package com.example.fullstackcrudreact.fullstackbackend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.security.CustomUserDetailsService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final CustomUserDetailsService customUserDetailsService; // Use your custom service

    public AuthController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/authme")
    public User getLoggedInUser() {
        // Get the username of the logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if the authentication is available (to avoid potential nulls if not authenticated)
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No user is currently authenticated");
        }

        // Retrieve user details using custom service
        String username = auth.getName();  // Get the logged-in username
        User user = (User) customUserDetailsService.loadUserByUsername(username); 
        
        // If user is not found, handle accordingly
        if (user == null) {
            throw new IllegalStateException("User not found");
        }
        
        return user;
    }
}