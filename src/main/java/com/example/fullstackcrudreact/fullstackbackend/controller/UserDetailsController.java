package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.model.UserDetails;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserDetailsRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserDetailsController {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 1. Get all user details
    @GetMapping("/userdetails")
    public List<UserDetails> getAllUserDetails() {
        return userDetailsRepository.findAll();
    }

    // ✅ 2. Get user details by userId
    @GetMapping("/userdetails/{userId}")
    public ResponseEntity<UserDetails> getUserDetailsById(@PathVariable Long userId) {
        Optional<UserDetails> userDetails = userDetailsRepository.findUserDetailsById(userId);
        return userDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ 3. Create user details (Ensure User Exists)
    @PostMapping("/adduserdetails/{userId}")
    public ResponseEntity<?> createUserDetails(@PathVariable Long userId, @RequestBody UserDetails userDetails) {

         try {
        // Check if User Exists   
         User user = userRepository.findById(userId)
         .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
     
           // ✅ Check if UserDetails already exists (to avoid duplicates)
        Optional<UserDetails> existingUserDetails = userDetailsRepository.findById(userId);
        if (existingUserDetails.isPresent()) {
            return ResponseEntity.badRequest().body("UserDetails already exists for user ID: " + userId);
        }

        // Prevent duplicate UserDetails
        if (userDetailsRepository.existsByUser(user)) {
            return ResponseEntity.badRequest().body("UserDetails already exists for the user with ID: " + userId);
        }

        // ✅ Ensure the correct ID mapping
        userDetails.setUser(user);
    
        // ✅ Save the new UserDetails entry
        UserDetails savedUserDetails = userDetailsRepository.save(userDetails);
        return ResponseEntity.ok(savedUserDetails);

    } catch (RuntimeException ex) {
        // Log the error and return a meaningful response
        System.err.println("Error creating UserDetails: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while creating UserDetails: " + ex.getMessage());
    }
    }

    // ✅ 4. Update user details
    @PutMapping("/userdetails/{userId}")
    public ResponseEntity<UserDetails> updateUserDetails(@PathVariable Long userId, @RequestBody UserDetails newDetails) {
        return userDetailsRepository.findUserDetailsById(userId)
                .map(existingDetails -> {
                    existingDetails.setPhoneNumber(newDetails.getPhoneNumber());
                    existingDetails.setAddress(newDetails.getAddress());
                    existingDetails.setDateOfBirth(newDetails.getDateOfBirth());
                    existingDetails.setSex(newDetails.getSex());
                    existingDetails.setCountryOfBirth(newDetails.getCountryOfBirth());
                    existingDetails.setCity(newDetails.getCity());
                    existingDetails.setZipCode(newDetails.getZipCode());

                    UserDetails updatedDetails = userDetailsRepository.save(existingDetails);
                    return ResponseEntity.ok(updatedDetails);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ 5. Delete user details by userId
    @DeleteMapping("/userdetails/{userId}")
    public ResponseEntity<Void> deleteUserDetails(@PathVariable Long userId) {
        if (!userDetailsRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userDetailsRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}


