package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.model.UserDetails;
import com.example.fullstackcrudreact.fullstackbackend.model.UserDetails.Sex;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserDetailsRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserDetailsController {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    @PostMapping(value = "/adduserdetails/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<?> createUserDetails(
        @PathVariable Long userId,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("address") String address,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("sex") String sex,
            @RequestParam("countryOfBirth") String countryOfBirth,
            @RequestParam("city") String city,
            @RequestParam("zipCode") String zipCode,
            @RequestParam("userImage") MultipartFile userImage) throws IOException {

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

        // ✅ Create new UserDetails object
        
        
         // ✅ Create new UserDetails object
         UserDetails userDetails = new UserDetails();
         userDetails.setPhoneNumber(phoneNumber);
         userDetails.setAddress(address);

           // Convert dateOfBirth string to LocalDate
            try {
                LocalDate birthDate = LocalDate.parse(dateOfBirth, dateFormatter);
                userDetails.setDateOfBirth(birthDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Invalid date format for dateOfBirth. Expected format is yyyy-MM-dd.");
            }

            // Convert sex string to Sex enum
            try {
                userDetails.setSex(Sex.valueOf(sex.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid value for sex. Expected values are MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY.");
            }


         userDetails.setCountryOfBirth(countryOfBirth);
         userDetails.setCity(city);
         userDetails.setZipCode(zipCode);

         // Save the image (as Base64)
         if (userImage != null && !userImage.isEmpty()) {
             userDetails.setUserImage(userImage.getBytes());
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
    @PutMapping(value = "/userdetails/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<UserDetails> updateUserDetails(
            @PathVariable Long userId,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("address") String address,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("sex") String sex,
            @RequestParam("countryOfBirth") String countryOfBirth,
            @RequestParam("city") String city,
            @RequestParam("zipCode") String zipCode,
            @RequestParam("userImage") MultipartFile userImage) {
        return userDetailsRepository.findUserDetailsById(userId)
                .map(existingDetails -> {
                    existingDetails.setPhoneNumber(phoneNumber);
                    existingDetails.setAddress(address);    

                    LocalDate birthDate = LocalDate.parse(dateOfBirth, dateFormatter);
                    existingDetails.setDateOfBirth(birthDate);    

                    existingDetails.setSex(Sex.valueOf(sex.toUpperCase()));    
                                 
                    existingDetails.setCountryOfBirth(countryOfBirth);
                    existingDetails.setCity(city);
                    existingDetails.setZipCode(zipCode);

                    // Update the image (as Base64)
                    if (userImage != null && !userImage.isEmpty()) {
                        try {
                            existingDetails.setUserImage(userImage.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

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


