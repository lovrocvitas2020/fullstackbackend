package com.example.fullstackcrudreact.fullstackbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fullstackcrudreact.fullstackbackend.exception.ResourceNotFoundException;
import com.example.fullstackcrudreact.fullstackbackend.model.UserDetails;
import com.example.fullstackcrudreact.fullstackbackend.model.UserDetailsDTO;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserDetailsRepository;

@Service
public class UserDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    // Get user details by ID
    public UserDetails getUserDetailsById(Long id) {
        return userDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserDetails not found with id: " + id));
    }

    // Create user details
    public UserDetails createUserDetails(UserDetailsDTO userDetailsDTO) {
        UserDetails userDetails = userDetailsDTO.toEntity();
        return userDetailsRepository.save(userDetails);
    }

    // Update user details
    public UserDetails updateUserDetails(Long id, UserDetailsDTO userDetailsDTO) {
        UserDetails existingDetails = userDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserDetails not found with id: " + id));

        // Update fields from DTO
        existingDetails.setPhoneNumber(userDetailsDTO.getPhoneNumber());
        existingDetails.setAddress(userDetailsDTO.getAddress());
        existingDetails.setDateOfBirth(userDetailsDTO.getDateOfBirth());
        System.out.println("Debug updateUserDetails userDetailsDTO.getSex().toUpperCase(): "+userDetailsDTO.getSex().toUpperCase());
        existingDetails.setSex(UserDetails.Sex.valueOf(userDetailsDTO.getSex().toUpperCase())); // Convert to enum
        existingDetails.setCountryOfBirth(userDetailsDTO.getCountryOfBirth());
        existingDetails.setCity(userDetailsDTO.getCity());
        existingDetails.setZipCode(userDetailsDTO.getZipCode());

        return userDetailsRepository.save(existingDetails);
    }

    


}
