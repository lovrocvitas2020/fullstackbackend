package com.example.fullstackcrudreact.fullstackbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.model.UserDetails;


public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

        // ✅ Find UserDetails by User ID (Since UserDetails uses @MapsId, ID matches User ID)
        Optional<UserDetails> findUserDetailsById(Long userId);

        // ✅ Check if UserDetails exists by User ID
        boolean existsById(Long userId);

        boolean existsByUser(User user);

  

}
