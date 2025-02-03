package com.example.fullstackcrudreact.fullstackbackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    
    User findByUsername(String username);

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable); // Added method for name search

    
}
